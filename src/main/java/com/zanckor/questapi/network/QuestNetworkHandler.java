package com.zanckor.questapi.network;

import com.zanckor.questapi.QuestApi;
import com.zanckor.questapi.network.messages.QuestData;
import com.zanckor.questapi.network.messages.QuestTimers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class QuestNetworkHandler {

    private static final String PROTOCOL_VERSION = "1.0";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(QuestApi.MOD_ID, "questapinetwork"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int index = 0;

        CHANNEL.messageBuilder(QuestData.class, index++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(QuestData::encodeBuffer).decoder(QuestData::new)
                .consumerNetworkThread(QuestData::handle).add();

        CHANNEL.messageBuilder(QuestTimers.class, index++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(QuestTimers::encodeBuffer).decoder(QuestTimers::new)
                .consumerNetworkThread(QuestTimers::handle).add();
    }
}
