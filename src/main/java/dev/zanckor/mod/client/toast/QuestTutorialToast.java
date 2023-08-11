package dev.zanckor.mod.client.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.zanckor.mod.client.screen.abstractscreen.AbstractQuestLog;
import dev.zanckor.mod.common.util.MCUtilClient;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import static dev.zanckor.mod.QuestApiMain.MOD_ID;

public class QuestTutorialToast extends TutorialToast {
    ResourceLocation BOOK = new ResourceLocation(MOD_ID, "textures/gui/book.png");
    private Visibility visibility = Visibility.SHOW;

    public QuestTutorialToast() {
        super(null, null, null, false);
    }

    @Override
    public Visibility render(GuiGraphics guiGraphics, ToastComponent component, long l) {
        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();

        //Render background
        RenderSystem.setShaderColor(0.3F, 0.3F, 0.3F, 0.9F);
        guiGraphics.blit(TEXTURE, 0, 0, 0, 96, this.width(), this.height());

        //Render book
        RenderSystem.setShaderColor(1, 1, 1, 1);
        guiGraphics.blit(BOOK, 6, 6, 0, 0, 20, 20, 20, 20, 20);

        //Render text
        MutableComponent title = Component.literal("Open Quest Log").withStyle(ChatFormatting.YELLOW);
        MutableComponent text = Component.literal("Press K to open").withStyle(ChatFormatting.WHITE);

        MCUtilClient.renderLine(guiGraphics, poseStack, 30, 6, 0, title, Minecraft.getInstance().font);
        MCUtilClient.renderLine(guiGraphics, poseStack, 30, 18, 0, text, Minecraft.getInstance().font);

        //Remove on show book
        Screen screen = Minecraft.getInstance().screen;

        if (screen != null && screen.getClass().getSuperclass().equals(AbstractQuestLog.class)) {
            visibility = Visibility.HIDE;

            //Complete toast when open quest log
            Player player = Minecraft.getInstance().player;
            player.getPersistentData().putBoolean("has_displayed_tutorial", true);
        }

        poseStack.popPose();

        return this.visibility;
    }
}
