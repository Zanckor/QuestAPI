package com.zanckor.mod.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.zanckor.api.quest.ClientQuestBase;
import com.zanckor.api.database.LocateHash;
import com.zanckor.api.quest.enumquest.EnumQuestRequirement;
import com.zanckor.api.quest.abstracquest.AbstractRequirement;
import com.zanckor.api.quest.ServerQuestBase;
import com.zanckor.api.quest.enumquest.EnumQuestType;
import com.zanckor.api.quest.register.TemplateRegistry;
import com.zanckor.mod.network.SendQuestPacket;
import com.zanckor.mod.network.message.screen.QuestTracked;
import com.zanckor.mod.util.MCUtil;
import com.zanckor.mod.util.Timer;
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

import static com.zanckor.api.quest.enumquest.EnumQuestType.PROTECT_ENTITY;
import static com.zanckor.mod.QuestApiMain.*;

public class QuestCommand {

    public static int trackedQuest(CommandContext<CommandSourceStack> context, UUID playerUUID, int questID) throws IOException {
        ServerLevel level = context.getSource().getLevel();
        Player player = level.getPlayerByUUID(playerUUID);
        String quest = "id_" + questID + ".json";
        Path userFolder = Paths.get(playerData.toString(), player.getUUID().toString());

        for (File file : getActiveQuest(userFolder).toFile().listFiles()) {
            Path path = Paths.get(getActiveQuest(userFolder).toString(), "\\", file.getName());
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            if (file.getName().equals(quest)) {
                FileReader reader = new FileReader(file);
                ClientQuestBase clientQuestBase = gson.fromJson(reader, ClientQuestBase.class);
                reader.close();

                SendQuestPacket.TO_CLIENT(player, new QuestTracked(clientQuestBase));
            }
        }

        return 1;
    }



    public static int addQuest(CommandContext<CommandSourceStack> context, UUID playerUUID, int questID) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ServerLevel level = context.getSource().getLevel();
        Player player = level.getPlayerByUUID(playerUUID);
        String quest = "id_" + questID + ".json";

        Path userFolder = Paths.get(playerData.toString(), player.getUUID().toString());

        if (Files.exists(Paths.get(getCompletedQuest(userFolder).toString(), quest)) || Files.exists(Paths.get(getActiveQuest(userFolder).toString(), quest)) || Files.exists(Paths.get(getUncompletedQuest(userFolder).toString(), quest))) {
            context.getSource().sendFailure(Component.literal("Player " + player.getScoreboardName() + " with UUID " + playerUUID + " already completed/has this quest"));

            return 0;
        }

        for (File file : serverQuests.toFile().listFiles()) {
            Path path = Paths.get(getActiveQuest(userFolder).toString(), "\\", file.getName());

            if (file.getName().equals(quest)) {
                ServerQuestBase serverQuest = MCUtil.getJsonServerQuest(file, gson);
                AbstractRequirement requirement = TemplateRegistry.getQuestRequirement(EnumQuestRequirement.valueOf(serverQuest.getRequirements_type()));

                if (!requirement.handler(player, serverQuest)) {
                    return 0;
                }

                FileWriter writer = new FileWriter(path.toFile());
                ClientQuestBase playerQuest = ClientQuestBase.createQuest(serverQuest, path);
                gson.toJson(playerQuest, writer);
                writer.close();

                if (playerQuest.isHasTimeLimit()) {
                    Timer.updateCooldown(playerUUID, "id_" + questID, playerQuest.getTimeLimitInSeconds());
                }

                if (playerQuest.getQuest_type().equals(PROTECT_ENTITY.toString())) {
                    protectEntityQuest(playerQuest, level, player, serverQuest, path, gson, questID);
                }

                LocateHash.registerQuestByID(questID, path);
                break;
            }
        }


        return 1;
    }

    private static int protectEntityQuest(ClientQuestBase playerQuest, ServerLevel level, Player player, ServerQuestBase serverQuest, Path path, Gson gson, int questID) throws IOException {
        EntityType entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(playerQuest.getQuest_target().get(0)));
        UUID playerUUID = player.getUUID();

        Entity entity = entityType.create(level);
        entity.setPos(player.getPosition(0));

        level.addFreshEntity(entity);

        FileWriter protectEntityWriter = new FileWriter(path.toFile());
        ClientQuestBase protectEntityPlayerQuest = ClientQuestBase.createQuest(serverQuest, path);
        List<String> list = new ArrayList<>();

        list.add(entity.getUUID().toString());

        protectEntityPlayerQuest.setQuest_target(list);
        gson.toJson(protectEntityPlayerQuest, protectEntityWriter);
        protectEntityWriter.close();

        Timer.updateCooldown(playerUUID, "id_" + questID, playerQuest.getTimeLimitInSeconds());

        return 1;
    }

    public static int removeQuest(CommandContext<CommandSourceStack> context, UUID playerUUID, int questID) throws IOException {
        Path path = LocateHash.getQuestByID(questID);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        File file = path.toFile();
        file.delete();

        FileReader reader = new FileReader(file);
        ServerQuestBase serverQuest = gson.fromJson(reader, ServerQuestBase.class);
        reader.close();

        LocateHash.removeQuest(questID, path, EnumQuestType.valueOf(serverQuest.getQuest_type()));

        return 1;
    }
}
