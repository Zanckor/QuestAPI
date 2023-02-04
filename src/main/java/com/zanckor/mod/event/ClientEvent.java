package com.zanckor.mod.event;

import com.zanckor.mod.ClientEventHandlerRegister;
import com.zanckor.mod.QuestApiMain;
import com.zanckor.mod.network.SendQuestPacket;
import com.zanckor.mod.network.message.screen.RequestQuestList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvent {

    @SubscribeEvent
    public static void keyOpenScreen(InputEvent.Key e) {
        if (ClientEventHandlerRegister.questMenu.isDown()) {
            SendQuestPacket.TO_SERVER(new RequestQuestList());
        }
    }
}
