package com.zanckor.mod.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.zanckor.questapi.createQuest.PlayerQuest;
import com.zanckor.api.quest_register.QuestTemplate;
import com.zanckor.mod.utils.QuestTimers;
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

import static com.zanckor.mod.QuestApiMain.*;

public class QuestCommands {

    public static int addQuest(CommandContext<CommandSourceStack> context, UUID playerUUID, int questID) throws IOException {
        ServerLevel level = context.getSource().getLevel();
        Player player = level.getPlayerByUUID(playerUUID);
        String quest = "id_" + questID + ".json";

        if (Files.exists(Paths.get(getCompletedQuest(playerData).toString(), quest)) || Files.exists(Paths.get(getActiveQuest(playerData).toString(), quest)) || Files.exists(Paths.get(getUncompletedQuest(playerData).toString(), quest))) {
            context.getSource().sendFailure(Component.literal("Player " + player.getScoreboardName() + " with UUID " + playerUUID + " already completed/has this quest"));

            return 0;
        }

        for (File file : serverQuests.toFile().listFiles()) {
            Path path = Paths.get(getActiveQuest(playerData).toString(), "\\", file.getName());
            Gson gson = new GsonBuilder().setPrettyPrinting().create();


            if (file.getName().equals(quest)) {
                FileReader reader = new FileReader(file);
                QuestTemplate abstractQuest = gson.fromJson(reader, QuestTemplate.class);
                reader.close();

                /*
                switch (abstractQuest.getRequirements_type()) {
                    case "xp" -> {
                        boolean minReqs = player.experienceLevel >= abstractQuest.getRequirements_min() ? true : false;
                        boolean maxReqs = player.experienceLevel <= abstractQuest.getRequirements_max() ? true : false;

                        if (!minReqs || !maxReqs) {
                            context.getSource().sendFailure(Component.literal( "Player " + player.getName() + " doesn't have the requirements to access to this quest"));
                            writer.close();
                            return 0;
                        }


                        break;
                    }
                }
                 */

                FileWriter writer = new FileWriter(path.toFile());
                PlayerQuest playerQuest = PlayerQuest.createQuest(abstractQuest);
                gson.toJson(playerQuest, writer);
                writer.close();

                if (playerQuest.hasTimeLimit) {
                    QuestTimers.updateCooldown(playerUUID, "id_" + questID, playerQuest.getTimeLimitInSeconds());
                }

                if (playerQuest.getQuest_type().equals("protect_entity")) {
                    EntityType entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(playerQuest.getQuest_target().get(0)));

                    Entity entity = entityType.create(level);
                    entity.setPos(player.getPosition(0));

                    level.addFreshEntity(entity);

                    FileWriter protectEntityWriter = new FileWriter(path.toFile());
                    PlayerQuest protectEntityPlayerQuest = PlayerQuest.createQuest(abstractQuest);
                    List<String> list = new ArrayList<>();

                    list.add(entity.getUUID().toString());

                    protectEntityPlayerQuest.setQuest_target(list);
                    gson.toJson(protectEntityPlayerQuest, protectEntityWriter);
                    protectEntityWriter.close();

                    QuestTimers.updateCooldown(playerUUID, "id_" + questID, playerQuest.getTimeLimitInSeconds());
                }

                break;
            }
        }

        return 1;
    }


    public static int removeQuest(CommandContext<CommandSourceStack> context, UUID playerUUID, int questID) throws IOException {
        Path userFolder = Paths.get(playerData.toString(), playerUUID.toString());

        for (File file : getActiveQuest(playerData).toFile().listFiles()) {
            if (file.getName().equals("id_" + questID + ".json")) {
                file.delete();
            }
        }

        for (File file : getCompletedQuest(playerData).toFile().listFiles()) {
            if (file.getName().equals("id_" + questID + ".json")) {
                file.delete();
            }
        }

        for (File file : getUncompletedQuest(playerData).toFile().listFiles()) {
            if (file.getName().equals("id_" + questID + ".json")) {
                file.delete();
            }
        }

        return 1;
    }
}
