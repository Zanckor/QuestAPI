package dev.zanckor.mod.common.network.message.screen;

import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.quest.ActiveQuestList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RequestActiveQuests {

    public RequestActiveQuests() {
    }

    public RequestActiveQuests(FriendlyByteBuf buffer) {

    }

    public void encodeBuffer(FriendlyByteBuf buffer) {

    }


    public static void handler(RequestActiveQuests msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            SendQuestPacket.TO_CLIENT(player, new ActiveQuestList(player.getUUID()));
        });

        ctx.get().setPacketHandled(true);
    }
}

