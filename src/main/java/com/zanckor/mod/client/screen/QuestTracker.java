package com.zanckor.mod.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zanckor.mod.network.ClientHandler;
import net.minecraft.client.Minecraft;

import static com.zanckor.mod.network.ClientHandler.trackedHasTimeLimit;
import static com.zanckor.mod.network.ClientHandler.trackedTitle;

public class QuestTracker {

    public static void renderQuestTracker(PoseStack poseStack, int width, int height, int questID) {
        int yPosition = 0;

        poseStack.pushPose();
        poseStack.translate(width / 32, height / 16, 0);
        poseStack.scale((float) ((width / 400) / 1.5), (float) ((width / 400) / 1.5), 1);


        Minecraft.getInstance().font.draw(poseStack, "Quest: " + trackedTitle, 0, 0, 0);
        Minecraft.getInstance().font.draw(poseStack, "Type: " + ClientHandler.trackedQuest_type, 0, 10, 0);

        for (int i = 0; i < ClientHandler.trackedQuest_target.size(); i++) {
            Minecraft.getInstance().font.draw(poseStack, "Target: " + ClientHandler.trackedQuest_target.get(i), 0, 30 + yPosition, 0);
            Minecraft.getInstance().font.draw(poseStack,
                    ClientHandler.trackedTarget_current_quantity.get(i).toString() + " / " + ClientHandler.trackedTarget_quantity.get(i).toString(),
                    ClientHandler.trackedQuest_target.get(i).length() * 5 + 50,
                    30 + yPosition, 0);

            yPosition += 10;
        }

        if (ClientHandler.trackedHasTimeLimit) {
            yPosition += 10;
            Minecraft.getInstance().font.draw(poseStack, "Time limit: " + String.valueOf(ClientHandler.trackedTimeLimitInSeconds), 0, 30 + yPosition, 0);
        }


        poseStack.popPose();
        Minecraft.getInstance().getProfiler().pop();
    }
}
