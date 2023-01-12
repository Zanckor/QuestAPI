package com.zanckor.questapi.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.zanckor.questapi.createQuest.ServerQuest;
import com.zanckor.questapi.createQuest.kill.ClientKillQuest;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Collectors;

public class QuestCommands {

    public static int addQuest(CommandContext<CommandSourceStack> context, UUID playerUUID, int questID) throws IOException {
        ServerLevel level = context.getSource().getLevel();
        Player player = level.getPlayerByUUID(playerUUID);

        Path serverDirectory = context.getSource().getServer().getServerDirectory().toPath();

        Path questapi = Paths.get(serverDirectory.toString(), "quest-api");
        Path playerdata = Paths.get(questapi.toString(), "player-data");
        Path userFolder = Paths.get(playerdata.toString(), playerUUID.toString());
        Path serverquests = Paths.get(questapi.toString(), "server-quests");


        for (File file : serverquests.toFile().listFiles()) {
            Path path = Paths.get(userFolder.toString(), "\\", file.getName());
            Gson gson = new GsonBuilder().setPrettyPrinting().create();


            if (file.getName().equals("id_" + questID + ".json")) {
                FileReader reader = new FileReader(file);

                ServerQuest abstractQuest = gson.fromJson(reader, ServerQuest.class);

                System.out.println(new BufferedReader(reader).lines().collect(Collectors.joining(System.lineSeparator())));

                ClientKillQuest playerQuest = ClientKillQuest.createQuest(abstractQuest);


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

                reader.close();

                FileWriter writer = new FileWriter(path.toFile());
                gson.toJson(playerQuest, writer);
                writer.close();

                break;
            }
        }

        return 1;
    }


    public static int removeQuest(CommandContext<CommandSourceStack> context, UUID playerUUID, int questID) throws IOException {
        Path serverDirectory = context.getSource().getServer().getServerDirectory().toPath();

        Path questapi = Paths.get(serverDirectory.toString(), "quest-api");
        Path playerdata = Paths.get(questapi.toString(), "player-data");
        Path userFolder = Paths.get(playerdata.toString(), playerUUID.toString());

        for (File file : userFolder.toFile().listFiles()) {
            if (file.getName().equals("id_" + questID + ".json")) {
                file.delete();
            }
        }

        return 1;
    }
}
