package dev.zanckor.example.server.event.questevent;

import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.common.network.handler.ServerHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

import static dev.zanckor.example.common.enumregistry.enumquest.EnumGoalType.KILL;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class KillEvent {
    @SubscribeEvent
    public static void killQuest(LivingDeathEvent e) throws IOException {
        if (!(e.getSource().getEntity() instanceof ServerPlayer player) || player.level.isClientSide) return;

        ServerHandler.questHandler(KILL, player, (LivingEntity)e.getEntity());
    }
}