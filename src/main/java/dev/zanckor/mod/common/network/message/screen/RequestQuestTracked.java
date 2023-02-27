package dev.zanckor.mod.common.network.message.screen;

import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.quest.UserQuest;
import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.util.GsonManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

public class RequestQuestTracked {
    private String questID;


    public RequestQuestTracked(String questID) {
        this.questID = questID;
    }

    public RequestQuestTracked(FriendlyByteBuf buffer) {
        questID = buffer.readUtf();
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
        buffer.writeUtf(questID);
    }


    public static void handler(RequestQuestTracked msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            File file = LocateHash.getQuestByID(msg.questID).toFile();

            try {
                UserQuest userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
                SendQuestPacket.TO_CLIENT(player, new QuestTracked(userQuest));

            } catch (IOException e) {
                QuestApiMain.LOGGER.error(e.getMessage());
            }
        });

        ctx.get().setPacketHandled(true);
    }
}

