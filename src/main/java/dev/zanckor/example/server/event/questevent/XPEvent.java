package dev.zanckor.example.server.event.questevent;

import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.common.network.handler.ServerHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

import static dev.zanckor.example.common.enumregistry.enumquest.EnumGoalType.XP;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class XPEvent {
    @SubscribeEvent
    public static void xpQuest(PlayerXpEvent e) throws IOException {
        if (!(e.getEntity() instanceof ServerPlayer player) || player.level.isClientSide) return;

        ServerHandler.questHandler(XP, player, (LivingEntity)e.getEntity());
    }
}