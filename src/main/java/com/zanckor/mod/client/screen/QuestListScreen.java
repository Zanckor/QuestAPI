package com.zanckor.mod.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zanckor.mod.QuestApiMain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuestListScreen extends Screen {
    private final static ResourceLocation QUEST_LOG = new ResourceLocation(QuestApiMain.MOD_ID,
            "textures/gui/questlog_api.png");

    private final static ResourceLocation EMPTY_TEXTURE = new ResourceLocation(QuestApiMain.MOD_ID,
            "textures/gui/empty_texture.png");

    double xScreenPos, yScreenPos;
    int imageWidth, imageHeight;

    HashMap<Integer, String> quest = new HashMap<>();

    public QuestListScreen(List<String> title) {
        super(Component.literal("questlist"));

        for (int i = 0; i < title.size(); i++) {
            quest.put(i, title.get(i));
        }
    }


    @Override
    protected void init() {
        super.init();

        imageWidth = width / 2;
        imageHeight = width / 3;

        xScreenPos = width - (imageWidth);
        yScreenPos = height / 4;

        int xPosition = (int) (xScreenPos - imageWidth);
        int yPosition = (int) yScreenPos;

        for (int i = 0; i < quest.size(); i++) {
            int index = i;

            ImageButton imageButton = new ImageButton(xPosition, yPosition,
                    width / 10, height / 40, width / 10, height / 40, 0,
                    QUEST_LOG, 0, 0,
                    button -> {
                        button(index);
                    });

            imageButton.setPosition(0, 0);

            addRenderableWidget(imageButton);

            yPosition += 22;
        }
    }


    private void button(int index) {
        System.out.println(quest.get(index));
    }


    @Override
    public void render(PoseStack poseStack, int x, int y, float partialTicks) {
        Minecraft.getInstance().getProfiler().push("background");
        RenderSystem.setShaderTexture(0, QUEST_LOG);

        blit(poseStack, (int) (xScreenPos - (imageWidth / 2)), (int) yScreenPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);

        renderQuestTitle(poseStack);
        renderQuestData(poseStack);

        Minecraft.getInstance().getProfiler().pop();

        super.render(poseStack, x, y, partialTicks);
    }

    public void renderQuestTitle(PoseStack poseStack) {
        poseStack.pushPose();

        int xPosition = (int) (xScreenPos - imageWidth);
        int yPosition = (int) yScreenPos;


        for (int i = 0; i < quest.size(); i++) {
            int index = i;

            addRenderableWidget(new ImageButton(xPosition, yPosition,
                    0, 0, 0, 0, 0,
                    EMPTY_TEXTURE, width / 20, height / 40,
                    button -> {
                        button(index);
                    }));

            poseStack.translate(xPosition, yPosition, 0);
            poseStack.scale(width / 400, width / 400, 1);

            font.draw(poseStack, quest.get(i),0, 0, 30000000);

            yPosition += 22;
        }

        poseStack.popPose();
    }

    public void renderQuestData(PoseStack poseStack) {
        poseStack.pushPose();

        String title = "Test";
        List<String> contentText = new ArrayList<>();

        contentText.add("Prueba 1");
        contentText.add("Prueba 2");
        contentText.add("Prueba 3");
        contentText.add("Prueba 4");

        int yTextPos = (int) (yScreenPos + 35);


        font.draw(poseStack, title, (float) xScreenPos - (title.length() / 2 * 5), (float) yScreenPos + 15, 0);

        poseStack.translate(xScreenPos - (imageWidth / 2 - title.length() / 2 * 5), yTextPos, 0);

        poseStack.scale(width / 400, width / 400, 1);

        for (int i = 0; i < contentText.size(); i++) {
            poseStack.translate(0, 15, 0);

            font.draw(poseStack, contentText.get(i), 0, 0, 0);

            yTextPos += 15;

        }

        poseStack.popPose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
