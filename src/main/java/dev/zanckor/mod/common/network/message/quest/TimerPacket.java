package dev.zanckor.mod.common.network.message.quest;

import dev.zanckor.api.filemanager.quest.UserQuest;
import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.common.util.GsonManager;
import dev.zanckor.mod.common.util.Timer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

import static dev.zanckor.mod.QuestApiMain.playerData;

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
                    try {
                        UserQuest userQuest = (UserQuest) GsonManager.getJson(file, UserQuest.class);

                        if (userQuest.isCompleted()) return;

                        if (userQuest.hasTimeLimit() && Timer.canUseWithCooldown(player.getUUID(), userQuest.getId(), userQuest.getTimeLimitInSeconds())) {
                            FileWriter writer = new FileWriter(file);
                            userQuest.setCompleted(true);

                            GsonManager.gson().toJson(userQuest, writer);
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

