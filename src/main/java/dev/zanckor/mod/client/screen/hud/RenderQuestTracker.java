package dev.zanckor.mod.client.screen.hud;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.zanckor.api.filemanager.quest.UserQuest.QuestGoal;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractTargetType;
import dev.zanckor.api.filemanager.quest.register.TemplateRegistry;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestType;
import dev.zanckor.example.common.handler.questtype.CompleteQuest;
import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.common.util.GsonManager;
import dev.zanckor.mod.common.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.zanckor.mod.common.network.ClientHandler.*;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class RenderQuestTracker {

    static float xPosition;
    static float yPosition;
    static float scale;

    @SubscribeEvent
    public static void tickEvent(TickEvent e) throws IOException {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || userQuest == null || !(CompleteQuest.isQuestCompleted(mc.player, GsonManager.gson(), userQuest)) || !(e.phase.equals(TickEvent.Phase.START)))
            return;

        if (mc.player.tickCount % 20 == 0 && questHasTimeLimit && Timer.existsTimer(mc.player.getUUID(), "TIMER_QUEST" + questID) && questTimeLimit > 0) {
            questTimeLimit = (int) Timer.remainingTime(mc.player.getUUID(), "TIMER_QUEST" + questID) / 1000;
        }
    }


    public static void renderQuestTracker(PoseStack poseStack, int width, int height) {
        Minecraft mc = Minecraft.getInstance();
        HashMap<String, List<QuestGoal>> userQuests = new HashMap<>();

        try {
            if (userQuest == null || CompleteQuest.isQuestCompleted(mc.player, GsonManager.gson(), userQuest)) {
                userQuests.clear();
                return;
            }

            xPosition = width / 100;
            yPosition = width / 100;
            scale = ((float) width) / 700;

            mc.getProfiler().push("hud_tracked");
            poseStack.pushPose();
            poseStack.scale(scale, scale, 1);

            for (QuestGoal questGoal : userQuest.getQuestGoals()) {
                String type = questGoal.getType();
                List<QuestGoal> questGoalList = userQuests.get(type);


                if (questGoalList == null) {
                    questGoalList = new ArrayList<>();
                }

                questGoalList.add(questGoal);

                userQuests.put(type, questGoalList);
            }

            mc.font.draw(poseStack, "Quest: " + questTitle, xPosition, yPosition, 0);

            for (Map.Entry<String, List<QuestGoal>> entry : userQuests.entrySet()) {
                yPosition += 20;
                List<QuestGoal> questGoalList = entry.getValue();

                mc.font.draw(poseStack, "Type: " + questGoalList.get(0).getType(), xPosition, yPosition, 0);

                for (QuestGoal questGoal : questGoalList) {
                    AbstractTargetType targetType = TemplateRegistry.getTargetType(EnumQuestType.valueOf(questGoal.getType()));
                    String translationKey = questGoal.getTarget();

                    if (targetType != null) {
                        translationKey = targetType.handler(new ResourceLocation(questGoal.getTarget()));
                    }

                    yPosition += 20;

                    mc.font.draw(poseStack, I18n.get(translationKey) + ": " + questGoal.getCurrentAmount() + "/" + questGoal.getAmount(), xPosition, yPosition, 0);
                }
            }

            if (questHasTimeLimit) {
                yPosition += 20;

                mc.font.draw(poseStack, "Time limit: " + questTimeLimit, xPosition, yPosition, 0);
            }

            poseStack.popPose();
            mc.getProfiler().pop();
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
    }
}