package dev.zanckor.example.server.event.questevent;

import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.common.network.ServerHandler;
import dev.zanckor.mod.common.util.Timer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

import static dev.zanckor.example.common.enumregistry.enumquest.EnumQuestType.INTERACT_ENTITY;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InteractWEntityEvent {

    @SubscribeEvent
    public static void interactWithNPC(PlayerInteractEvent.EntityInteract e) throws IOException {
        if (e.getHand() == InteractionHand.OFF_HAND || e.getSide().isClient()) return;

        if (Timer.canUseWithCooldown(e.getEntity().getUUID(), "INTERACT_EVENT_COOLDOWN", 1)) {
            ServerHandler.questHandler(INTERACT_ENTITY, (ServerPlayer) e.getEntity(), (LivingEntity) e.getTarget());
            Timer.updateCooldown(e.getEntity().getUUID(), "INTERACT_EVENT_COOLDOWN", 1);
        }
    }
}