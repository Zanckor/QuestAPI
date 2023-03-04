package dev.zanckor.example.client.screen.hud;

import dev.zanckor.example.common.handler.questtype.CompleteQuest;
import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.common.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

import static dev.zanckor.mod.common.network.handler.ClientHandler.*;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class QuestTrackedTimer {
    @SubscribeEvent
    public static void tickEvent(TickEvent e) throws IOException {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || userQuest == null || CompleteQuest.isQuestCompleted(userQuest) || !(e.phase.equals(TickEvent.Phase.START))) {
            return;
        }


        if (mc.player.tickCount % 20 == 0 && questHasTimeLimit && Timer.existsTimer(mc.player.getUUID(), "TIMER_QUEST" + questID) && questTimeLimit > 0) {
            questTimeLimit = (int) Timer.remainingTime(mc.player.getUUID(), "TIMER_QUEST" + questID) / 1000;
            System.out.println(questTimeLimit);
        }
    }
}
