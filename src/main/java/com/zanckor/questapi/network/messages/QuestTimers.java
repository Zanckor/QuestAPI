package com.zanckor.questapi.network.messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zanckor.questapi.QuestApi;
import com.zanckor.questapi.createQuest.PlayerQuest;
import com.zanckor.questapi.utils.Timer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

public class QuestTimers {

    public QuestTimers() {
    }

    public QuestTimers(FriendlyByteBuf buffer) {
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
    }


    public static void handle(QuestTimers msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            for (Player player : ctx.get().getSender().level.players()) {
                Path serverDirectory = ctx.get().getSender().server.getServerDirectory().toPath();

                Path questapi = Paths.get(serverDirectory.toString(), "quest-api");
                Path playerdata = Paths.get(questapi.toString(), "player-data");
                Path userFolder = Paths.get(playerdata.toString(), player.getUUID().toString());

                for (File file : userFolder.toFile().listFiles()) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();

                    try {
                        FileReader reader = new FileReader(file);
                        PlayerQuest playerQuest = gson.fromJson(reader, PlayerQuest.class);
                        reader.close();

                        if (playerQuest.completed) return;

                        if (playerQuest.hasTimeLimit && Timer.canUseWithCooldown(player.getUUID(), "id_" + playerQuest.getId(), playerQuest.getTimeLimitInSeconds())) {
                            FileWriter writer = new FileWriter(file);
                            playerQuest.setCompleted(true);

                            gson.toJson(playerQuest, writer);
                        }

                    } catch (IOException exception) {
                        QuestApi.LOGGER.error("File reader/writer error");
                    }
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}

