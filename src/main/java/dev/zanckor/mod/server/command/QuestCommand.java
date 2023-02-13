package dev.zanckor.mod.server.command;

import com.google.gson.Gson;
import com.mojang.brigadier.context.CommandContext;
import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.quest.UserQuest;
import dev.zanckor.api.filemanager.quest.ServerQuest;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractQuestRequirement;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestRequirement;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestType;
import dev.zanckor.api.filemanager.quest.register.TemplateRegistry;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.screen.QuestTracked;
import dev.zanckor.mod.common.network.message.screen.RemovedQuest;
import dev.zanckor.mod.common.util.GsonManager;
import dev.zanckor.mod.common.util.Timer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static dev.zanckor.example.common.enumregistry.enumquest.EnumQuestType.PROTECT_ENTITY;
import static dev.zanckor.mod.QuestApiMain.*;

public class QuestCommand {

    public static int trackedQuest(CommandContext<CommandSourceStack> context, UUID playerUUID, String questID) throws IOException {
        ServerLevel level = context.getSource().getLevel();
        Player player = level.getPlayerByUUID(playerUUID);
        String quest = questID + ".json";
        Path userFolder = Paths.get(playerData.toString(), player.getUUID().toString());

        for (File file : getActiveQuest(userFolder).toFile().listFiles()) {
            if (file.getName().equals(quest)) {
                UserQuest userQuest = (UserQuest) GsonManager.getJson(file, UserQuest.class);


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
            Path path = Paths.get(getActiveQuest(userFolder).toString(), "\\", file.getName());

            if (file.getName().equals(quest)) {
                ServerQuest serverQuest = (ServerQuest) GsonManager.getJson(file, ServerQuest.class);
                AbstractQuestRequirement requirement = TemplateRegistry.getQuestRequirement(EnumQuestRequirement.valueOf(serverQuest.getRequirements_type()));

                if (!requirement.handler(player, serverQuest)) {
                    return 0;
                }

                FileWriter writer = new FileWriter(path.toFile());
                UserQuest playerQuest = UserQuest.createQuest(serverQuest, path);
                GsonManager.gson().toJson(playerQuest, writer);
                writer.close();

                if (playerQuest.hasTimeLimit()) {
                    Timer.updateCooldown(playerUUID, questID, playerQuest.getTimeLimitInSeconds());
                }

                if (playerQuest.getQuest_type().equals(PROTECT_ENTITY.toString())) {
                    protectEntityQuest(playerQuest, level, player, serverQuest, path, GsonManager.gson(), questID);
                }

                LocateHash.registerQuestByID(questID, path);
                trackedQuest(context, playerUUID, questID);
                return 1;
            }
        }

        return 1;
    }

    private static int protectEntityQuest(UserQuest playerQuest, ServerLevel level, Player player, ServerQuest serverQuest, Path path, Gson gson, String questID) throws IOException {
        EntityType entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(playerQuest.getQuest_target().get(0)));
        UUID playerUUID = player.getUUID();

        Entity entity = entityType.create(level);
        entity.setPos(player.getPosition(0));

        level.addFreshEntity(entity);

        FileWriter protectEntityWriter = new FileWriter(path.toFile());
        UserQuest protectEntityPlayerQuest = UserQuest.createQuest(serverQuest, path);
        List<String> list = new ArrayList<>();

        list.add(entity.getUUID().toString());

        protectEntityPlayerQuest.setQuest_target(list);
        gson.toJson(protectEntityPlayerQuest, protectEntityWriter);
        protectEntityWriter.close();

        Timer.updateCooldown(playerUUID, questID, playerQuest.getTimeLimitInSeconds());

        return 1;
    }

    public static int removeQuest(CommandContext<CommandSourceStack> context, UUID playerUUID, String questID) throws IOException {
        ServerLevel level = context.getSource().getLevel();
        Player player = level.getPlayerByUUID(playerUUID);

        Path path = LocateHash.getQuestByID(questID);

        FileReader reader = new FileReader(path.toFile());
        UserQuest clientQuest = GsonManager.gson().fromJson(reader, UserQuest.class);
        reader.close();

        SendQuestPacket.TO_CLIENT(player, new RemovedQuest(clientQuest.getId()));
        LocateHash.removeQuest(questID, EnumQuestType.valueOf(clientQuest.getQuest_type()));
        path.toFile().delete();

        return 1;
    }
}
