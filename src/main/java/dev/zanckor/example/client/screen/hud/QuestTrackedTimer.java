package dev.zanckor.example.client.screen.hud;

import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.common.network.handler.ClientHandler;
import dev.zanckor.mod.common.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

import static dev.zanckor.mod.common.network.handler.ClientHandler.trackedQuestList;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class QuestTrackedTimer {
    @SubscribeEvent
    public static void tickEvent(TickEvent e) throws IOException {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || trackedQuestList.isEmpty() || !(e.phase.equals(TickEvent.Phase.START))) {
            return;
        }

        ClientHandler.trackedQuestList.forEach(quest -> {
            if (mc.player.tickCount % 20 == 0 && quest.hasTimeLimit() && Timer.existsTimer(mc.player.getUUID(), "TIMER_QUEST" + quest.getId()) && quest.getTimeLimitInSeconds() > 0) {
                quest.setTimeLimitInSeconds((int) Timer.remainingTime(mc.player.getUUID(), "TIMER_QUEST" + quest.getId()) / 1000);
            }
        });
    }
}
