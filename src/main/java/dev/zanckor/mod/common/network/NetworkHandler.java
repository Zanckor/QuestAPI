package dev.zanckor.mod.common.network;

import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.common.network.message.dialogoption.AddQuest;
import dev.zanckor.mod.common.network.message.dialogoption.CloseDialog;
import dev.zanckor.mod.common.network.message.dialogoption.DialogRequestPacket;
import dev.zanckor.mod.common.network.message.dialogoption.DisplayDialog;
import dev.zanckor.mod.common.network.message.npcmarker.ValidNPCMarker;
import dev.zanckor.mod.common.network.message.quest.*;
import dev.zanckor.mod.common.network.message.screen.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {

    private static final String PROTOCOL_VERSION = "2.1";

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

        CHANNEL.messageBuilder(RequestActiveQuests.class, index++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(RequestActiveQuests::encodeBuffer).decoder(RequestActiveQuests::new)
                .consumerNetworkThread(RequestActiveQuests::handler).add();

        CHANNEL.messageBuilder(DialogRequestPacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(DialogRequestPacket::encodeBuffer).decoder(DialogRequestPacket::new)
                .consumerNetworkThread(DialogRequestPacket::handler).add();

        CHANNEL.messageBuilder(AddQuest.class, index++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(AddQuest::encodeBuffer).decoder(AddQuest::new)
                .consumerNetworkThread(AddQuest::handler).add();

        CHANNEL.messageBuilder(OpenVanillaEntityScreen.class, index++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(OpenVanillaEntityScreen::encodeBuffer).decoder(OpenVanillaEntityScreen::new)
                .consumerNetworkThread(OpenVanillaEntityScreen::handler).add();

        CHANNEL.messageBuilder(ToastPacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ToastPacket::encodeBuffer).decoder(ToastPacket::new)
                .consumerNetworkThread(ToastPacket::handler).add();

        CHANNEL.messageBuilder(DisplayDialog.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(DisplayDialog::encodeBuffer).decoder(DisplayDialog::new)
                .consumerNetworkThread(DisplayDialog::handler).add();

        CHANNEL.messageBuilder(ModifyTrackedQuests.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ModifyTrackedQuests::encodeBuffer).decoder(ModifyTrackedQuests::new)
                .consumerNetworkThread(ModifyTrackedQuests::handler).add();

        CHANNEL.messageBuilder(CloseDialog.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(CloseDialog::encodeBuffer).decoder(CloseDialog::new)
                .consumerNetworkThread(CloseDialog::handler).add();

        CHANNEL.messageBuilder(UpdateQuestTracked.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(UpdateQuestTracked::encodeBuffer).decoder(UpdateQuestTracked::new)
                .consumerNetworkThread(UpdateQuestTracked::handler).add();

        CHANNEL.messageBuilder(RemovedQuest.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(RemovedQuest::encodeBuffer).decoder(RemovedQuest::new)
                .consumerNetworkThread(RemovedQuest::handler).add();

        CHANNEL.messageBuilder(ValidNPCMarker.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ValidNPCMarker::encodeBuffer).decoder(ValidNPCMarker::new)
                .consumerNetworkThread(ValidNPCMarker::handler).add();

        CHANNEL.messageBuilder(ActiveQuestList.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ActiveQuestList::encodeBuffer).decoder(ActiveQuestList::new)
                .consumerNetworkThread(ActiveQuestList::handler).add();

        CHANNEL.messageBuilder(ServerQuestList.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ServerQuestList::encodeBuffer).decoder(ServerQuestList::new)
                .consumerNetworkThread(ServerQuestList::handler).add();
    }
}
