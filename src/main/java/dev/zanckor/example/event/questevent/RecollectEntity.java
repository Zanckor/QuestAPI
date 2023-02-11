package dev.zanckor.example.event.questevent;

import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.network.SendQuestPacket;
import dev.zanckor.mod.network.message.quest.QuestDataPacket;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

import static dev.zanckor.example.enumregistry.enumquest.EnumQuestType.RECOLLECT;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RecollectEntity {

    @SubscribeEvent
    public static void recollectPickUpQuest(PlayerEvent.ItemPickupEvent e) throws IOException {
        sendQuestPacket();
    }

    @SubscribeEvent
    public static void recollectCraftQuest(PlayerEvent.ItemCraftedEvent e) {
        sendQuestPacket();
    }

    @SubscribeEvent
    public static void recollectCraftQuest(PlayerEvent.ItemSmeltedEvent e) {
        sendQuestPacket();
    }

    public static void sendQuestPacket() {
        SendQuestPacket.TO_SERVER(new QuestDataPacket(RECOLLECT));
    }
}
