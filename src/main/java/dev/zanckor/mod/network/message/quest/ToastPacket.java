package dev.zanckor.mod.network.message.quest;

import dev.zanckor.mod.network.ClientHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToastPacket {

    String questName;

    public ToastPacket(String questName) {
        this.questName = questName;
    }

    public ToastPacket(FriendlyByteBuf buffer) {
        this.questName = buffer.readUtf();
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
        buffer.writeUtf(questName);
    }


    public static void handler(ToastPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.toastQuestCompleted(msg.questName));
        });

        ctx.get().setPacketHandled(true);
    }
}
