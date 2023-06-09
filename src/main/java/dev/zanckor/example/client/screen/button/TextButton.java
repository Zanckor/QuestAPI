package dev.zanckor.example.client.screen.button;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.zanckor.mod.common.util.MCUtilClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TextButton extends Button {
    float scale;
    int maxLength;

    public TextButton(int xPosition, int yPosition, int width, int height, float scale, Component component, int maxLength, OnPress onPress) {
        super(xPosition, yPosition, width, height, component, onPress, DEFAULT_NARRATION);
        this.scale = scale;
        this.maxLength = maxLength;
    }


    @Override
    public void render(GuiGraphics graphics, int xPos, int yPos, float v) {
        PoseStack poseStack = graphics.pose();

        poseStack.pushPose();
        poseStack.translate(this.getX(), this.getY(), 0);
        poseStack.scale(scale, scale, 1);

        MCUtilClient.renderLines(graphics, poseStack, 16, 0, maxLength, getMessage().getString(), Minecraft.getInstance().font);
        poseStack.popPose();
    }
}
