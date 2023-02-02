package com.zanckor.mod.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zanckor.api.quest.abstracquest.AbstractTargetType;
import com.zanckor.api.quest.enumquest.EnumQuestType;
import com.zanckor.api.quest.register.TemplateRegistry;
import com.zanckor.mod.QuestApiMain;
import com.zanckor.mod.network.ClientHandler;
import com.zanckor.mod.util.MCUtil;
import com.zanckor.mod.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

import static com.zanckor.mod.network.ClientHandler.*;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class RenderQuestTracker {
    static List<String> questData = new ArrayList<>();

    @SubscribeEvent
    public static void tickEvent(TickEvent e) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || trackedTarget_quantity == null || trackedTarget_quantity.equals(trackedTarget_current_quantity) || e.phase.equals(TickEvent.Phase.START))
            return;

        if (mc.player.tickCount % 20 == 0) {
            if (trackedHasTimeLimit && Timer.existsTimer(mc.player.getUUID(), "TIMER_QUEST" + trackedID) && trackedTimeLimitInSeconds > 0) {
                int timer = (int) Timer.remainingTime(mc.player.getUUID(), "TIMER_QUEST" + trackedID) / 1000;
                trackedTimeLimitInSeconds = timer;
            }
        }
    }


    public static void renderQuestTracker(PoseStack poseStack, int width, int height, int questID) {
        if (trackedTitle == null || trackedTarget_quantity.equals(trackedTarget_current_quantity)) return;
        Minecraft mc = Minecraft.getInstance();

        mc.getProfiler().push("hud_tracked");

        questData.add("Quest: " + trackedTitle);
        questData.add("Type: " + trackedID.substring(0, 1).toUpperCase() + trackedQuest_type.substring(1).toLowerCase());

        for (int i = 0; i < trackedQuest_target.size(); i++) {
            AbstractTargetType targetType = TemplateRegistry.getTargetType(EnumQuestType.valueOf(trackedQuest_type));

            if (targetType != null) {
                String translationKey = targetType.handler(new ResourceLocation(trackedQuest_target.get(i)));
                questData.add(I18n.get(translationKey) + ": " + trackedTarget_current_quantity.get(i) + "/" + trackedTarget_quantity.get(i));
            } else {
                questData.add(trackedQuest_target.get(i) + ": " + trackedTarget_current_quantity.get(i) + "/" + trackedTarget_quantity.get(i));
            }
        }

        if (trackedHasTimeLimit) questData.add("Time limit: " + trackedTimeLimitInSeconds);

        MCUtil.renderText(poseStack, 0, 0, 20, ((float) width) / 575, 23, questData, mc.font);

        mc.getProfiler().pop();
    }
}
