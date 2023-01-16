package com.zanckor.mod.network.messages;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

public class QuestData {

    String quest;

    public QuestData(String quest) {
        this.quest = quest;
    }

    public QuestData(FriendlyByteBuf buffer) {
        this.quest = buffer.readUtf();
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
        buffer.writeUtf(quest);
    }


    public static void handle(QuestData msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();

            Path serverDirectory = ctx.get().getSender().server.getServerDirectory().toPath();

            Path questapi = Paths.get(serverDirectory.toString(), "quest-api");
            Path playerdata = Paths.get(questapi.toString(), "player-data");
            Path userFolder = Paths.get(playerdata.toString(), player.getUUID().toString());
            Path activeQuest = Paths.get(userFolder.toString(), "active-quests");
        });

        ctx.get().setPacketHandled(true);

    }
}


