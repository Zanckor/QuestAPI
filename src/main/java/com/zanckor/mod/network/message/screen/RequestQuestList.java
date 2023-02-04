package com.zanckor.mod.network.message.screen;

import com.zanckor.mod.network.SendQuestPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Supplier;

public class RequestQuestList {

    public RequestQuestList() {
    }

    public RequestQuestList(FriendlyByteBuf buffer) {
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
    }


    public static void handler(RequestQuestList msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();

            try {
                SendQuestPacket.TO_CLIENT(player, new QuestList(player.getUUID()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}