package dev.zanckor.mod.common.util;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import dev.zanckor.mod.QuestApiMain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, value = Dist.CLIENT)
public class MCUtilClient {

    public static List<List<FormattedCharSequence>> splitText(String text, Font font, int textMaxLength) {
        final List<List<FormattedCharSequence>> textBlocks = new ArrayList<>();

        textBlocks.add(font.split(Component.literal(text), textMaxLength));

        return textBlocks;
    }


    public static void playSound(SoundEvent sound, float minPitch, float maxPitch) {
        SoundManager soundManager = Minecraft.getInstance().getSoundManager();

        soundManager.play(SimpleSoundInstance.forUI(sound, Mth.randomBetween(RandomSource.create(), minPitch, maxPitch)));
    }

    public static void renderText(PoseStack poseStack, double width, double height, float textIndent, float scale, int textMaxLength, String text, Font font) {
        float splitIndent = 0;
        List<List<FormattedCharSequence>> splintedText = splitText(text, font, textMaxLength * 5);

        poseStack.pushPose();

        poseStack.translate(width, height, 0);
        poseStack.scale(scale, scale, 1);

        for (List<FormattedCharSequence> line : splintedText) {
            for (FormattedCharSequence lineString : line) {
                font.draw(poseStack, lineString, 0, textIndent * (splitIndent / 2), 0);

                splitIndent++;
            }
        }

        poseStack.popPose();
    }

    public static void renderText(PoseStack poseStack, double width, double height, float textIndent, float scale, int textMaxLength, List<String> text, Font font) {
        if (text == null) return;
        float splitIndent = 0;

        if (text.size() > 6) {
            for (int i = 0; i < text.size() - 6; i++) {
                scale *= 0.85;
            }
        }


        poseStack.pushPose();

        poseStack.translate(width, height, 0);
        poseStack.scale(scale, scale, 1);

        for (int i = 0; i < text.size(); i++) {
            for (List<FormattedCharSequence> textBlock : MCUtilClient.splitText(text.get(i), font, 5 * textMaxLength)) {
                for (FormattedCharSequence line : textBlock) {
                    if (splitIndent < 2) {
                        font.draw(poseStack, line, 0, textIndent * (i + (splitIndent / 2)), 0);
                        splitIndent++;
                    }

                    if (textBlock.size() >= 2 && textBlock.get(textBlock.size() - 1).equals(line)) {
                        poseStack.translate(0, textIndent / 2, 0);
                    }
                }

                splitIndent++;
            }

            splitIndent = 0;
        }


        poseStack.popPose();
    }

    public static void renderEntity(double xPos, double yPos, double size, double xRot, double yRot, LivingEntity entity) {
        float f = (float) Math.atan(xRot / 40.0F);
        float f1 = (float) Math.atan(yRot / 40.0F);
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.translate(xPos, yPos, 1050.0D);
        posestack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack posestack1 = new PoseStack();
        posestack1.translate(0.0D, 0.0D, 1000.0D);
        posestack1.scale((float) size, (float) size, (float) size);
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        posestack1.mulPose(quaternion);
        float f2 = entity.yBodyRot;
        float f3 = entity.getYRot();
        float f4 = entity.getXRot();
        float f5 = entity.yHeadRotO;
        float f6 = entity.yHeadRot;
        entity.yBodyRot = 180.0F;
        entity.setYRot(180.0F + f * 40.0F);
        entity.setXRot(-f1 * 20.0F);
        entity.yHeadRot = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        entityrenderdispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> entityrenderdispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, posestack1, multibuffersource$buffersource, 15728880));
        multibuffersource$buffersource.endBatch();
        entityrenderdispatcher.setRenderShadow(true);
        entity.yBodyRot = f2;
        entity.setYRot(f3);
        entity.setXRot(f4);
        entity.yHeadRotO = f5;
        entity.yHeadRot = f6;
        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }
}