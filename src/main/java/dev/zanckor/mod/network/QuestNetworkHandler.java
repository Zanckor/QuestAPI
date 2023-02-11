package dev.zanckor.mod.network;

import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.network.message.dialogoption.AddQuest;
import dev.zanckor.mod.network.message.dialogoption.CloseDialog;
import dev.zanckor.mod.network.message.dialogoption.DialogRequestPacket;
import dev.zanckor.mod.network.message.dialogoption.DisplayDialog;
import dev.zanckor.mod.network.message.quest.QuestDataPacket;
import dev.zanckor.mod.network.message.quest.TimerPacket;
import dev.zanckor.mod.network.message.quest.ToastPacket;
import dev.zanckor.mod.network.message.screen.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class QuestNetworkHandler {

    private static final String PROTOCOL_VERSION = "1.0";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(QuestApiMain.MOD_ID, "questapinetwork"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int index = 0;

        CHANNEL.messageBuilder(QuestDataPacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(QuestDataPacket::encodeBuffer).decoder(QuestDataPacket::new)
                .consumerNetworkThread(QuestDataPacket::handler).add();

        CHANNEL.messageBuilder(TimerPacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(TimerPacket::encodeBuffer).decoder(TimerPacket::new)
                .consumerNetworkThread(TimerPacket::handler).add();


        CHANNEL.messageBuilder(RequestQuestTracked.class, index++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(RequestQuestTracked::encodeBuffer).decoder(RequestQuestTracked::new)
                .consumerNetworkThread(RequestQuestTracked::handler).add();

        CHANNEL.messageBuilder(DialogRequestPacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(DialogRequestPacket::encodeBuffer).decoder(DialogRequestPacket::new)
                .consumerNetworkThread(DialogRequestPacket::handler).add();

        CHANNEL.messageBuilder(AddQuest.class, index++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(AddQuest::encodeBuffer).decoder(AddQuest::new)
                .consumerNetworkThread(AddQuest::handler).add();

        CHANNEL.messageBuilder(RequestQuestList.class, index++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(RequestQuestList::encodeBuffer).decoder(RequestQuestList::new)
                .consumerNetworkThread(RequestQuestList::handler).add();


        CHANNEL.messageBuilder(ToastPacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ToastPacket::encodeBuffer).decoder(ToastPacket::new)
                .consumerNetworkThread(ToastPacket::handler).add();


        CHANNEL.messageBuilder(DisplayDialog.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(DisplayDialog::encodeBuffer).decoder(DisplayDialog::new)
                .consumerNetworkThread(DisplayDialog::handler).add();

        CHANNEL.messageBuilder(CloseDialog.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(CloseDialog::encodeBuffer).decoder(CloseDialog::new)
                .consumerNetworkThread(CloseDialog::handler).add();

        CHANNEL.messageBuilder(QuestTracked.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(QuestTracked::encodeBuffer).decoder(QuestTracked::new)
                .consumerNetworkThread(QuestTracked::handler).add();

        CHANNEL.messageBuilder(RemovedQuest.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(RemovedQuest::encodeBuffer).decoder(RemovedQuest::new)
                .consumerNetworkThread(RemovedQuest::handler).add();

        CHANNEL.messageBuilder(QuestList.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(QuestList::encodeBuffer).decoder(QuestList::new)
                .consumerNetworkThread(QuestList::handler).add();
    }
}
