package dev.zanckor.example.client.screen.button;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.zanckor.mod.common.util.MCUtilClient;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;

public class TextButton extends Button {
    float scale;
    int maxLength;

    public TextButton(int xPosition, int yPosition, int width, int height, float scale, Component component, int maxLength, OnPress onPress) {
        super(xPosition, yPosition, width, height, component, onPress);
        this.scale = scale;
        this.maxLength = maxLength;
    }


    @Override
    public void render(PoseStack poseStack, int xPos, int yPos, float v) {
        poseStack.pushPose();
        Style style = !isMouseOver(xPos, yPos) ? getMessage().getStyle() : getMessage().getStyle().withUnderlined(true);

        poseStack.translate(this.x, this.y, 0);
        poseStack.scale(scale, scale, 1);

        MCUtilClient.renderLine(poseStack, 0, 3, maxLength, new TextComponent(getMessage().getString()).withStyle(style), Minecraft.getInstance().font);
        poseStack.popPose();
    }
}
