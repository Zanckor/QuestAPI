package dev.zanckor.example.server.event.questevent;

import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.quest.QuestDataPacket;
import dev.zanckor.mod.common.util.Timer;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestType;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InteractWEntityEvent {

    @SubscribeEvent
    public static void interactWithNPC(PlayerInteractEvent.EntityInteract e) {
        if (e.getHand() == InteractionHand.OFF_HAND) return;

        if(Timer.canUseWithCooldown(e.getEntity().getUUID(), "INTERACT_EVENT_COOLDOWN", 1)) {
            SendQuestPacket.TO_SERVER(new QuestDataPacket(EnumQuestType.INTERACT_ENTITY));
            Timer.updateCooldown(e.getEntity().getUUID(), "INTERACT_EVENT_COOLDOWN", 1);
        }
    }
}