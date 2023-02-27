package dev.zanckor.mod.server.command;

import com.mojang.brigadier.context.CommandContext;
import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.quest.ServerQuest;
import dev.zanckor.api.filemanager.quest.UserQuest;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractQuestRequirement;
import dev.zanckor.api.filemanager.quest.register.TemplateRegistry;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestRequirement;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestType;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.screen.QuestTracked;
import dev.zanckor.mod.common.network.message.screen.RemovedQuest;
import dev.zanckor.mod.common.util.GsonManager;
import dev.zanckor.mod.common.util.Timer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static dev.zanckor.mod.QuestApiMain.*;

public class QuestCommand {

    public static int trackedQuest(CommandContext<CommandSourceStack> context, UUID playerUUID, String questID) throws IOException {
        ServerLevel level = context.getSource().getLevel();
        Player player = level.getPlayerByUUID(playerUUID);
        String quest = questID + ".json";
        Path userFolder = Paths.get(playerData.toString(), player.getUUID().toString());

        for (File file : getActiveQuest(userFolder).toFile().listFiles()) {
            if (file.getName().equals(quest)) {
                UserQuest userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);


                SendQuestPacket.TO_CLIENT(player, new QuestTracked(userQuest));
            }
        }

        return 1;
    }


    public static int addQuest(CommandContext<CommandSourceStack> context, UUID playerUUID, String questID) throws IOException {
        ServerLevel level = context.getSource().getLevel();
        Player player = level.getPlayerByUUID(playerUUID);
        String quest = questID + ".json";

        Path userFolder = Paths.get(playerData.toString(), player.getUUID().toString());

        if (Files.exists(Paths.get(getCompletedQuest(userFolder).toString(), quest)) || Files.exists(Paths.get(getActiveQuest(userFolder).toString(), quest)) || Files.exists(Paths.get(getUncompletedQuest(userFolder).toString(), quest))) {
            context.getSource().sendFailure(Component.literal("Player " + player.getScoreboardName() + " with UUID " + playerUUID + " already completed/has this quest"));
            return 0;
        }


        for (File file : serverQuests.toFile().listFiles()) {
            if (!(file.getName().equals(quest))) continue;
            Path path = Paths.get(getActiveQuest(userFolder).toString(), "\\", file.getName());
            ServerQuest serverQuest = (ServerQuest) GsonManager.getJsonClass(file, ServerQuest.class);

            //Checks if player has all requirements
            for (int requirementIndex = 0; requirementIndex < serverQuest.getRequirements().size(); requirementIndex++) {
                AbstractQuestRequirement requirement = TemplateRegistry.getQuestRequirement(EnumQuestRequirement.valueOf(serverQuest.getRequirements().get(requirementIndex).getType()));

                if (!requirement.handler(player, serverQuest, requirementIndex)) {
                    return 0;
                }
            }

            createQuest(serverQuest, player, level, path);
            LocateHash.registerQuestByID(questID, path);
            trackedQuest(context, playerUUID, questID);
            return 1;
        }

        return 0;
    }

    private static int createQuest(ServerQuest serverQuest, Player player, Level level, Path path) throws IOException {
        UserQuest userQuest = UserQuest.createQuest(serverQuest, path);

        if (userQuest.hasTimeLimit()) {
            Timer.updateCooldown(player.getUUID(), userQuest.getId(), userQuest.getTimeLimitInSeconds());
        }

        FileWriter writer = new FileWriter(path.toFile());
        GsonManager.gson().toJson(userQuest, writer);
        writer.flush();
        writer.close();

        return 1;
    }

    public static int removeQuest(CommandContext<CommandSourceStack> context, UUID playerUUID, String questID) throws IOException {
        ServerLevel level = context.getSource().getLevel();
        Player player = level.getPlayerByUUID(playerUUID);

        Path path = LocateHash.getQuestByID(questID);

        FileReader reader = new FileReader(path.toFile());
        UserQuest userQuest = GsonManager.gson().fromJson(reader, UserQuest.class);
        reader.close();

        SendQuestPacket.TO_CLIENT(player, new RemovedQuest(userQuest.getId()));

        for (int indexGoals = 0; indexGoals < userQuest.getQuestGoals().size(); indexGoals++) {
            UserQuest.QuestGoal questGoal = userQuest.getQuestGoals().get(indexGoals);

            LocateHash.removeQuest(questID, EnumQuestType.valueOf(questGoal.getType()));
        }

        path.toFile().delete();

        return 1;
    }
}
