package com.zanckor.example.event.questevent;

import com.zanckor.mod.QuestApiMain;
import com.zanckor.mod.network.SendQuestPacket;
import com.zanckor.mod.network.message.quest.QuestDataPacket;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

import static com.zanckor.api.quest.enumquest.EnumQuestType.RECOLLECT;
import static com.zanckor.example.event.dialogevent.StartDialogEvent.loadDialog;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RecollectEntity {

    /*TODO
     * estoy ejecutando temporalmente el loadDialog desde aqui, estoy trabajando en meter un npc
     * que te lo ponga al dar click derecho,
     * tambien dando click derecho a un item, y lo que se me ocurra
     * Por ahora es al recolectar un item
     */


    @SubscribeEvent
    public static void recollectPickUpQuest(PlayerEvent.ItemPickupEvent e) throws IOException {
        //Como no está el sistema de npcs y tal se puede cambiar desde globalDialogID
        loadDialog(e.getEntity(), 0);

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
