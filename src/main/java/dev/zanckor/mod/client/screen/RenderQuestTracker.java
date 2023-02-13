package dev.zanckor.mod.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractTargetType;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestType;
import dev.zanckor.api.filemanager.quest.register.TemplateRegistry;
import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.common.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static dev.zanckor.mod.common.network.ClientHandler.*;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class RenderQuestTracker {
    static float xPosition;
    static float yPosition;
    static float scale;

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
        if (trackedTitle == null || trackedTarget_quantity.equals(trackedTarget_current_quantity) || trackedQuest_completed) return;
        Minecraft mc = Minecraft.getInstance();

        xPosition = width / 100;
        yPosition = width / 100;

        scale = ((float) width) / 700;

        mc.getProfiler().push("hud_tracked");
        poseStack.pushPose();

        poseStack.scale(scale, scale, 1);

        mc.font.draw(poseStack,
                "Quest: " + trackedTitle,
                xPosition, yPosition, 0
        );

        yPosition += 20;

        for (int i = 0; i < trackedQuest_target.size(); i++) {
            AbstractTargetType targetType = TemplateRegistry.getTargetType(EnumQuestType.valueOf(trackedQuest_type));

            if (targetType != null) {
                String translationKey = targetType.handler(new ResourceLocation(trackedQuest_target.get(i)));


                mc.font.draw(poseStack,
                        I18n.get(translationKey) + ": " + trackedTarget_current_quantity.get(i) + "/" + trackedTarget_quantity.get(i),
                        xPosition, yPosition, 0
                );
            } else {
                mc.font.draw(poseStack,
                        trackedQuest_target.get(i) + ": " + trackedTarget_current_quantity.get(i) + "/" + trackedTarget_quantity.get(i),
                        xPosition, yPosition, 0
                );
            }

            yPosition += 20;
        }

        if (trackedHasTimeLimit) {
            mc.font.draw(poseStack,
                    "Time limit: " + trackedTimeLimitInSeconds,
                    xPosition, yPosition, 0
            );
        }

        poseStack.popPose();
        mc.getProfiler().pop();
    }
}
