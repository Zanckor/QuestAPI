package dev.zanckor.mod.server.command;

import com.mojang.brigadier.context.CommandContext;
import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractQuestRequirement;
import dev.zanckor.api.filemanager.quest.codec.server.ServerQuest;
import dev.zanckor.api.filemanager.quest.codec.user.UserGoal;
import dev.zanckor.api.filemanager.quest.codec.user.UserQuest;
import dev.zanckor.api.filemanager.quest.register.LoadQuest;
import dev.zanckor.api.filemanager.quest.register.QuestTemplateRegistry;
import dev.zanckor.example.common.enumregistry.EnumRegistry;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.quest.ActiveQuestList;
import dev.zanckor.mod.common.network.message.quest.ServerQuestList;
import dev.zanckor.mod.common.network.message.screen.RemovedQuest;
import dev.zanckor.mod.common.util.GsonManager;
import dev.zanckor.mod.common.util.Timer;
import dev.zanckor.mod.server.displaydialog.StartDialog;
import dev.zanckor.mod.server.menu.questmaker.QuestMakerMenu;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static dev.zanckor.mod.QuestApiMain.*;

public class QuestCommand {
    public static int reloadQuests(CommandContext<CommandSourceStack> context, String identifier) {
        LoadQuest.registerQuest(context.getSource().getServer(), identifier);

        return 1;
    }

    public static int addQuest(CommandContext<CommandSourceStack> context, UUID playerUUID, String questID) throws IOException {
        ServerLevel level = context.getSource().getPlayer().getLevel();
        Player player = level.getPlayerByUUID(playerUUID);
        String quest = questID + ".json";

        Path userFolder = Paths.get(playerData.toString(), player.getUUID().toString());

        if (Files.exists(Paths.get(getCompletedQuest(userFolder).toString(), quest)) || Files.exists(Paths.get(getActiveQuest(userFolder).toString(), quest)) || Files.exists(Paths.get(getFailedQuest(userFolder).toString(), quest))) {
            context.getSource().sendFailure(Component.literal("Player " + player.getScoreboardName() + " with UUID " + playerUUID + " already completed/has this quest"));
            return 0;
        }


        for (File file : serverQuests.toFile().listFiles()) {
            if (!(file.getName().equals(quest))) continue;
            Path path = Paths.get(getActiveQuest(userFolder).toString(), File.separator, file.getName());
            ServerQuest serverQuest = (ServerQuest) GsonManager.getJsonClass(file, ServerQuest.class);

            //Checks if player has all requirements
            for (int requirementIndex = 0; requirementIndex < serverQuest.getRequirements().size(); requirementIndex++) {
                Enum questRequirementEnum = EnumRegistry.getEnum(serverQuest.getRequirements().get(requirementIndex).getType(), EnumRegistry.getQuestRequirement());
                AbstractQuestRequirement requirement = QuestTemplateRegistry.getQuestRequirement(questRequirementEnum);

                if (!requirement.handler(player, serverQuest, requirementIndex)) {
                    return 0;
                }
            }

            createQuest(serverQuest, player, path);
            LocateHash.registerQuestByID(questID, path);
            SendQuestPacket.TO_CLIENT(player, new ActiveQuestList(player.getUUID()));
            return 1;
        }

        return 0;
    }

    public static int createQuest(ServerQuest serverQuest, Player player, Path path) throws IOException {
        UserQuest userQuest = UserQuest.createQuest(serverQuest, path);

        if (userQuest.hasTimeLimit()) {
            Timer.updateCooldown(player.getUUID(), userQuest.getId(), userQuest.getTimeLimitInSeconds());
        }

        GsonManager.writeJson(path.toFile(), userQuest);

        return 1;
    }

    public static int removeQuest(CommandContext<CommandSourceStack> context, UUID playerUUID, String questID) throws IOException {
        ServerLevel level = context.getSource().getPlayer().getLevel();
        Player player = level.getPlayerByUUID(playerUUID);
        Path path = LocateHash.getQuestByID(questID);
        UserQuest userQuest = (UserQuest) GsonManager.getJsonClass(path.toFile(), UserQuest.class);

        SendQuestPacket.TO_CLIENT(player, new RemovedQuest(userQuest.getId()));

        for (int indexGoals = 0; indexGoals < userQuest.getQuestGoals().size(); indexGoals++) {
            UserGoal questGoal = userQuest.getQuestGoals().get(indexGoals);
            Enum goalEnum = EnumRegistry.getEnum(questGoal.getType(), EnumRegistry.getQuestGoal());

            LocateHash.removeQuest(questID, goalEnum);
        }

        path.toFile().delete();

        SendQuestPacket.TO_CLIENT(player, new ActiveQuestList(player.getUUID()));
        return 1;
    }

    public static int putDialogToItem(ItemStack item, String dialogId){
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("display_dialog", dialogId);

        item.setTag(compoundTag);

        return 1;
    }

    public static int putQuestToItem(ItemStack item, String questId){
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("give_quest", questId);

        item.setTag(compoundTag);

        return 1;
    }

    public static int displayDialog(ServerPlayer player, String dialogID) throws IOException {
        StartDialog.loadDialog(player, dialogID, player);

        return 1;
    }


    public static int openQuestMaker(CommandContext<CommandSourceStack> context){
        SimpleMenuProvider menuProvider =
                new SimpleMenuProvider((id, inventory, p) -> new QuestMakerMenu(id), Component.literal("quest_default_menu"));

        SendQuestPacket.TO_CLIENT(context.getSource().getPlayer(), new ServerQuestList());
        NetworkHooks.openScreen(context.getSource().getPlayer(), menuProvider);
        return 1;
    }
}
