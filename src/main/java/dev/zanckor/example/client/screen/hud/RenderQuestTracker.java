package dev.zanckor.example.client.screen.hud;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.zanckor.api.filemanager.quest.UserQuest.QuestGoal;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractTargetType;
import dev.zanckor.api.filemanager.quest.register.TemplateRegistry;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestType;
import dev.zanckor.example.common.handler.CompleteQuest;
import dev.zanckor.mod.client.screen.AbstractQuestTracked;
import dev.zanckor.mod.common.util.MCUtilClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.zanckor.mod.common.network.handler.ClientHandler.*;
import static dev.zanckor.mod.common.util.MCUtilClient.properNoun;

public class RenderQuestTracker extends AbstractQuestTracked {

    static float xPosition;
    static float yPosition;
    static float scale;


    public void renderQuestTracked(PoseStack poseStack, int width, int height) {
        Minecraft mc = Minecraft.getInstance();
        HashMap<String, List<QuestGoal>> userQuests = new HashMap<>();

        try {
            if (userQuest == null || CompleteQuest.isQuestCompleted(userQuest) || mc.player.isReducedDebugInfo() || mc.options.keyPlayerList.isDown() || mc.options.renderDebug) {
                userQuests.clear();
                return;
            }

            xPosition = width / 100;
            yPosition = width / 100;
            scale = ((float) width) / 700;


            mc.getProfiler().push("hud_tracked");
            poseStack.pushPose();
            poseStack.scale(scale, scale, 1);

            //Gets all quest types on json and creates a HashMap with a list of goals
            for (QuestGoal questGoal : userQuest.getQuestGoals()) {
                String type = questGoal.getType();
                List<QuestGoal> questGoalList = userQuests.get(type);


                if (questGoalList == null) {
                    questGoalList = new ArrayList<>();
                }

                questGoalList.add(questGoal);

                userQuests.put(type, questGoalList);
            }

            //Displays quest goals
            MCUtilClient.renderLine(poseStack, (int) xPosition, (int) yPosition, 20, "Quest: " + properNoun(questTitle), mc.font);

            for (Map.Entry<String, List<QuestGoal>> entry : userQuests.entrySet()) {
                List<QuestGoal> questGoalList = entry.getValue();
                MCUtilClient.renderLine(poseStack, (int) xPosition, (int) yPosition, 10, "Quest Type: " + properNoun(questGoalList.get(0).getType()), mc.font);

                for (QuestGoal questGoal : questGoalList) {
                    AbstractTargetType targetType = TemplateRegistry.getTargetType(EnumQuestType.valueOf(questGoal.getType()));
                    String translationKey = questGoal.getTarget();
                    if (targetType != null)
                        translationKey = targetType.handler(new ResourceLocation(questGoal.getTarget()));

                    MCUtilClient.renderLine(poseStack, (int) xPosition, (int) yPosition, 10, properNoun(I18n.get(translationKey)) + ": " + questGoal.getCurrentAmount() + "/" + questGoal.getAmount(), mc.font);
                }

                poseStack.translate(0, 10, 0);
            }

            if (questHasTimeLimit) {
                MCUtilClient.renderLine(poseStack, (int) xPosition, (int) yPosition, 10, "Time limit: " + questTimeLimit, mc.font);
            }

            poseStack.popPose();
            mc.getProfiler().pop();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}