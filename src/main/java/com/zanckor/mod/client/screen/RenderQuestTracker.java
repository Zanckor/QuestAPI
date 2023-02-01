package com.zanckor.mod.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zanckor.mod.QuestApiMain;
import com.zanckor.mod.network.ClientHandler;
import com.zanckor.mod.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.zanckor.mod.network.ClientHandler.*;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class RenderQuestTracker {

    @SubscribeEvent
    public static void tickEvent(TickEvent e) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || trackedTarget_quantity == null || trackedTarget_quantity.equals(trackedTarget_current_quantity) || e.phase.equals(TickEvent.Phase.START))
            return;

        if (mc.player.tickCount % 20 == 0) {
            if (trackedHasTimeLimit && Timer.existsTimer(mc.player.getUUID(), "TIMER_QUEST" + trackedTitle) && trackedTimeLimitInSeconds > 0) {
                int timer = (int) Timer.remainingTime(mc.player.getUUID(), "TIMER_QUEST" + trackedTitle) / 1000;
                trackedTimeLimitInSeconds = timer;
            }
        }
    }


    public static void renderQuestTracker(PoseStack poseStack, int width, int height, int questID) {
        if (trackedTitle == null || trackedTarget_quantity.equals(trackedTarget_current_quantity)) return;
        Minecraft mc = Minecraft.getInstance();

        int yPosition = 0;

        poseStack.pushPose();
        poseStack.translate(width / 32, height / 16, 0);
        poseStack.scale((float) ((width / 400) / 1.5), (float) ((width / 400) / 1.5), 1);


        mc.font.draw(poseStack, "Quest: " + trackedTitle, 0, 0, 0);
        mc.font.draw(poseStack, "Type: " + ClientHandler.trackedQuest_type, 0, 10, 0);

        for (int i = 0; i < ClientHandler.trackedQuest_target.size(); i++) {
            mc.font.draw(poseStack, "Target: " + ClientHandler.trackedQuest_target.get(i), 0, 30 + yPosition, 0);
            mc.font.draw(poseStack,
                    ClientHandler.trackedTarget_current_quantity.get(i).toString() + " / " + ClientHandler.trackedTarget_quantity.get(i).toString(),
                    ClientHandler.trackedQuest_target.get(i).length() * 5 + 50,
                    30 + yPosition, 0);

            yPosition += 10;
        }

        if (ClientHandler.trackedHasTimeLimit) {
            yPosition += 10;
            mc.font.draw(poseStack, "Time limit: " + ClientHandler.trackedTimeLimitInSeconds, 0, 30 + yPosition, 0);
        }


        poseStack.popPose();
        mc.getProfiler().pop();
    }
}
