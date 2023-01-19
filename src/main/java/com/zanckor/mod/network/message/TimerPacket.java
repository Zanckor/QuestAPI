package com.zanckor.mod.network.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zanckor.mod.QuestApiMain;
import com.zanckor.api.questregister.abstrac.PlayerQuest;
import com.zanckor.mod.util.Timer;
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

import static com.zanckor.mod.QuestApiMain.playerData;

public class TimerPacket {

    public TimerPacket() {
    }

    public TimerPacket(FriendlyByteBuf buffer) {
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
    }


    public static void handler(TimerPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            for (Player player : ctx.get().getSender().level.players()) {
                Path userFolder = Paths.get(playerData.toString(), player.getUUID().toString());

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
                        QuestApiMain.LOGGER.error(exception.getMessage());
                    }
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}

