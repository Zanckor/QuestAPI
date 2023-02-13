package dev.zanckor.mod.common.util;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.zanckor.mod.QuestApiMain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, value = Dist.CLIENT)
public class MCUtilClient {

    public static List<List<FormattedCharSequence>> splitText(String text, Font font, int textSize) {
        final List<List<FormattedCharSequence>> textBlocks = new ArrayList<>();

        textBlocks.add(font.split(Component.literal(text), textSize));

        return textBlocks;
    }


    public static void playSound(SoundEvent sound, float minPitch, float maxPitch) {
        SoundManager soundManager = Minecraft.getInstance().getSoundManager();

        soundManager.play(SimpleSoundInstance.forUI(sound, Mth.randomBetween(RandomSource.create(), minPitch, maxPitch)));
    }

    public static void renderText(PoseStack poseStack, double width, double height, float textIndent, float scale, int textMaxLength, String text, Font font) {
        float splitIndent = 0;


        poseStack.pushPose();

        poseStack.translate(width, height, 0);
        poseStack.scale(scale, scale, 1);

        for (List<FormattedCharSequence> textBlock : MCUtilClient.splitText(text, font, 5 * textMaxLength)) {
            for (FormattedCharSequence line : textBlock) {
                if (splitIndent < 2) {
                    font.draw(poseStack, line, 0, textIndent * (splitIndent / 2), 0);
                    splitIndent++;
                }
            }

            splitIndent++;
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
}