package com.zanckor.mod.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zanckor.mod.QuestApiMain;
import com.zanckor.mod.network.ClientHandler;
import com.zanckor.mod.util.MCUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuestListScreen extends Screen {
    private final static ResourceLocation QUEST_LOG = new ResourceLocation(QuestApiMain.MOD_ID, "textures/gui/questlog_api.png");

    private final static ResourceLocation EMPTY_TEXTURE = new ResourceLocation(QuestApiMain.MOD_ID, "textures/gui/empty_texture.png");

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

            ImageButton imageButton = new ImageButton(xPosition, yPosition, width / 10, height / 40, width / 10, height / 40, 0, QUEST_LOG, 0, 0, button -> {
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

        renderQuestTitles(poseStack);
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

            addRenderableWidget(new ImageButton(xPosition, yPosition, 0, 0, 0, 0, 0, EMPTY_TEXTURE, width / 20, height / 40, button -> {
                button(index);
            }));

            poseStack.translate(xPosition, yPosition, 0);
            poseStack.scale(width / 400, width / 400, 1);

            font.draw(poseStack, quest.get(i), 0, 0, 30000000);

            yPosition += 22;


        }

        poseStack.popPose();
    }


    public void renderQuestTitles(PoseStack poseStack) {
        List<String> questList = new ArrayList<>();

        for (int i = 0; i < quest.size(); i++) {
            if (i < 4) questList.add(quest.get(i));
        }

        MCUtil.renderText(poseStack, xScreenPos - (imageWidth / 2.3), yScreenPos / 0.62, 20, width / 400, 18, questList, font);
    }

    public void renderQuestData(PoseStack poseStack) {
        List<String> list = new ArrayList<>();


        list.add("Type: " + ClientHandler.trackedQuest_type);

        for (int i = 0; i < ClientHandler.trackedQuest_target.size(); i++) {
            Item itemTarget = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ClientHandler.trackedQuest_target.get(i)));
            String translationKey = itemTarget.getDescriptionId(itemTarget.getDefaultInstance());

            list.add(I18n.get(translationKey) + ": " + ClientHandler.trackedTarget_current_quantity.get(i) + "/" + ClientHandler.trackedTarget_quantity.get(i));
        }

        if (ClientHandler.trackedHasTimeLimit) list.add("Time limit: " + ClientHandler.trackedTimeLimitInSeconds);


        MCUtil.renderText(poseStack, xScreenPos + (imageWidth / 24), yScreenPos / 0.62, 20, width / 400, 18, list, font);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
