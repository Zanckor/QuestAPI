package com.zanckor.mod.network.message.screen;

import com.zanckor.api.database.LocateHash;
import com.zanckor.mod.QuestApiMain;
import com.zanckor.mod.network.SendQuestPacket;
import com.zanckor.mod.util.MCUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

public class RequestQuestTracked {
    private int questID;


    public RequestQuestTracked(int questID) {
        this.questID = questID;
    }

    public RequestQuestTracked(FriendlyByteBuf buffer) {
        questID = buffer.readInt();
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
        buffer.writeInt(questID);
    }


    public static void handler(RequestQuestTracked msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            File file = LocateHash.getQuestByID(msg.questID).toFile();

            try {
                SendQuestPacket.TO_CLIENT(player, new QuestTracked(MCUtil.getJsonClientQuest(file)));
            } catch (IOException e) {
                QuestApiMain.LOGGER.error(e.getMessage());
            }
        });

        ctx.get().setPacketHandled(true);
    }
}

