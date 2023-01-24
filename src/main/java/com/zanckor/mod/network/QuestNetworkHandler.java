package com.zanckor.mod.network;

import com.zanckor.mod.QuestApiMain;
import com.zanckor.mod.network.message.DialogRequestPacket;
import com.zanckor.mod.network.message.QuestDataPacket;
import com.zanckor.mod.network.message.TimerPacket;
import com.zanckor.mod.network.message.ToastPacket;
import com.zanckor.mod.network.message.dialog.AddQuest;
import com.zanckor.mod.network.message.dialog.CloseDialog;
import com.zanckor.mod.network.message.dialog.DisplayDialog;
import com.zanckor.mod.network.message.screen.QuestTracked;
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


        CHANNEL.messageBuilder(DialogRequestPacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(DialogRequestPacket::encodeBuffer).decoder(DialogRequestPacket::new)
                .consumerNetworkThread(DialogRequestPacket::handler).add();

        CHANNEL.messageBuilder(AddQuest.class, index++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(AddQuest::encodeBuffer).decoder(AddQuest::new)
                .consumerNetworkThread(AddQuest::handler).add();


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
    }
}
