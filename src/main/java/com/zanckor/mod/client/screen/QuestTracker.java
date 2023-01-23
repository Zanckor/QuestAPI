package com.zanckor.mod.client.screen;

import ca.weblite.objc.Client;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zanckor.mod.network.ClientHandler;
import net.minecraft.client.Minecraft;

import static com.zanckor.mod.network.ClientHandler.trackedTitle;

public class QuestTracker {

    public static void renderQuestTracker(PoseStack poseStack, int width, int questID) {
        int yPosition = 0;

        poseStack.pushPose();
        poseStack.translate(0, width / 16.5, 0);
        poseStack.scale((float) ((width / 400) / 1.5), (float) ((width / 400) / 1.5), 1);


        System.out.println(trackedTitle);

        Minecraft.getInstance().font.draw(poseStack, trackedTitle, 0, 0, 0);
        Minecraft.getInstance().font.draw(poseStack, ClientHandler.trackedQuest_type, 0, 10, 0);

        for(int i = 0; i < ClientHandler.trackedQuest_target.size(); i++) {
            Minecraft.getInstance().font.draw(poseStack, ClientHandler.trackedQuest_target.get(i), 0, 30 + yPosition, 0);

            yPosition += 10;
        }

        yPosition += 10;

        yPosition += 10;
        Minecraft.getInstance().font.draw(poseStack, String.valueOf(ClientHandler.trackedHasTimeLimit), 0, 30 + yPosition, 0);

        yPosition += 10;
        Minecraft.getInstance().font.draw(poseStack, String.valueOf(ClientHandler.trackedTimeLimitInSeconds), 0, 30 + yPosition, 0);


        poseStack.popPose();
        Minecraft.getInstance().getProfiler().pop();
    }
}
