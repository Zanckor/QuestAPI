package dev.zanckor.example.client.screen.hud;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractTargetType;
import dev.zanckor.api.filemanager.quest.codec.user.UserGoal;
import dev.zanckor.api.filemanager.quest.register.QuestTemplateRegistry;
import dev.zanckor.example.common.enumregistry.EnumRegistry;
import dev.zanckor.mod.client.screen.abstractscreen.AbstractQuestTracked;
import dev.zanckor.mod.common.util.MCUtil;
import dev.zanckor.mod.common.util.MCUtilClient;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.zanckor.mod.common.network.handler.ClientHandler.*;

public class RenderQuestTracker extends AbstractQuestTracked {

    static float xPosition;
    static float yPosition;
    static float scale;
    static float sin;


    public void renderQuestTracked(PoseStack poseStack, int width, int height) {
        Minecraft minecraft = Minecraft.getInstance();
        HashMap<String, List<UserGoal>> userQuestHashMap = new HashMap<>();

        if (userQuest == null || userQuest.isCompleted() || minecraft.player.isReducedDebugInfo() || minecraft.options.keyPlayerList.isDown() || minecraft.options.renderDebug) {
            userQuestHashMap.clear();
            return;
        }
        xPosition = width / 100;
        yPosition = width / 100;
        scale = ((float) width) / 700;


        minecraft.getProfiler().push("hud_tracked");
        poseStack.pushPose();
        poseStack.scale(scale, scale, 1);

        //Gets all quest types on json and creates a HashMap with a list of goals
        for (UserGoal questGoal : userQuest.getQuestGoals()) {
            String type = questGoal.getType();
            List<UserGoal> questGoalList = userQuestHashMap.get(type);


            if (questGoalList == null) {
                questGoalList = new ArrayList<>();
            }

            questGoalList.add(questGoal);

            userQuestHashMap.put(type, questGoalList);
        }

        //Displays quest goals
        renderTitle(poseStack, minecraft);
        renderQuestType(poseStack, minecraft, userQuestHashMap);

        if (questHasTimeLimit) {
            MCUtilClient.renderLine(poseStack, (int) xPosition, (int) yPosition, 10, I18n.get("tracker.questapi.time_limit") + questTimeLimit, minecraft.font);
        }

        poseStack.popPose();
        minecraft.getProfiler().pop();
    }

    public static void renderTitle(PoseStack poseStack, Minecraft minecraft) {
        //If quest title on quest.json starts with # means that is a translatable string, else is rendered literally

        String title = I18n.get(questTitle);

        MCUtilClient.renderLine(poseStack, (int) xPosition, (int) yPosition, 20,
                Component.literal(I18n.get("tracker.questapi.quest") + title).withStyle(ChatFormatting.WHITE), minecraft.font);
    }

    public static void renderQuestType(PoseStack poseStack, Minecraft minecraft, HashMap<String, List<UserGoal>> userQuestHashMap) {
        Font font = minecraft.font;
        Player player = minecraft.player;
        sin += 0.5;


        for (Map.Entry<String, List<UserGoal>> entry : userQuestHashMap.entrySet()) {
            List<UserGoal> questGoalList = entry.getValue();

            //Render quest type
            MCUtilClient.renderLine(poseStack, Integer.MAX_VALUE, xPosition, yPosition, 10,
                    Component.literal(I18n.get("tracker.questapi.quest_type") +
                            I18n.get("quest_type." + questGoalList.get(0).getTranslatableType().toLowerCase())).withStyle(ChatFormatting.WHITE), font);

            //Render each quest goal of a single type and render target
            for (UserGoal questGoal : questGoalList) {
                Enum goalEnum = EnumRegistry.getEnum(questGoal.getType(), EnumRegistry.getQuestGoal());

                AbstractTargetType translatableTargetType = QuestTemplateRegistry.getTranslatableTargetType(goalEnum);
                MutableComponent goalComponentTarget = translatableTargetType.handler(questGoal.getTarget(), questGoal, player, ChatFormatting.GRAY, ChatFormatting.BLACK);

                translatableTargetType.renderTarget(poseStack, (int) (goalComponentTarget.getString().length() * 6 + xPosition - 4), (int) yPosition + 3, 0.7, Math.sin(sin), questGoal, questGoal.getTarget());
                MCUtilClient.renderLine(poseStack, 30, yPosition, yPosition, 10, goalComponentTarget.withStyle(ChatFormatting.ITALIC), font);
            }

            poseStack.translate(0, 10, 0);
        }
    }
}