package com.zanckor.mod.network.message.screen;

import com.zanckor.api.quest.ClientQuestBase;
import com.zanckor.mod.network.ClientHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RemovedQuest {
    private String title;


    public RemovedQuest(String questTitle) {
        this.title = questTitle;
    }

    public RemovedQuest(FriendlyByteBuf buffer) {
        title = buffer.readUtf();
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
        buffer.writeUtf(title);
    }


    public static void handler(RemovedQuest msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.removeQuest(msg.title));
        });

        ctx.get().setPacketHandled(true);
    }
}

