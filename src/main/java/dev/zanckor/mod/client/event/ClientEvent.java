package dev.zanckor.mod.client.event;

import dev.zanckor.mod.ClientEventHandlerRegister;
import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.screen.RequestQuestList;
import dev.zanckor.mod.common.util.MCUtil;
import dev.zanckor.mod.common.util.MCUtilClient;
import net.minecraft.client.Minecraft;
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
