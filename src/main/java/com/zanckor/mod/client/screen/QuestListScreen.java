package com.zanckor.mod.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zanckor.api.quest.abstracquest.AbstractTargetType;
import com.zanckor.api.quest.enumquest.EnumQuestType;
import com.zanckor.api.quest.register.TemplateRegistry;
import com.zanckor.mod.QuestApiMain;
import com.zanckor.mod.network.SendQuestPacket;
import com.zanckor.mod.network.message.screen.RequestQuestTracked;
import com.zanckor.mod.util.MCUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

import static com.zanckor.mod.network.ClientHandler.*;

public class QuestListScreen extends Screen {
    private final static ResourceLocation QUEST_LOG = new ResourceLocation(QuestApiMain.MOD_ID, "textures/gui/questlog_api.png");
    List<String> list = new ArrayList<>();
    int selectedPage = 0;


    double xScreenPos, yScreenPos;
    int imageWidth, imageHeight;

    HashMap<Integer, Map.Entry<Integer, String>> quest = new HashMap<>();

    public QuestListScreen(List<Integer> id, List<String> title) {
        super(Component.literal("questlist"));

        for (int i = 0; i < title.size(); i++) {
            quest.put(i, new AbstractMap.SimpleEntry(id.get(i), title.get(i)));
        }
    }


    @Override
    protected void init() {
        super.init();
        clearWidgets();

        imageWidth = width / 2;
        imageHeight = width / 3;
        xScreenPos = width - (imageWidth);
        yScreenPos = height / 4;

        float scale = ((float) width) / 575;

        int buttonIndent = 16;
        int maxLength = 22 * 5;
        float splitIndent = 0;

        int xButtonPosition = (int) (xScreenPos - (imageWidth / 2.4));
        int yButtonPosition = (int) (yScreenPos * 1.6);

        for (int i = 0; i < quest.size(); i++) {
            if (i < 4 && quest.size() > (i + (4 * (selectedPage)))) {
                int buttonIndex = i + (4 * (selectedPage));
                int index = buttonIndex;

                int textLines = (quest.get(buttonIndex).getValue().length() * 5) / maxLength;
                int yButtonIndent = (int) (((8 * (textLines + 1)) * i * scale) + splitIndent);

                int buttonWidth = textLines < 1 ? (int) (quest.get(buttonIndex).getValue().length() * 5 * scale) : (int) (maxLength * scale);

                Button questSelect = new Button(xButtonPosition, yButtonPosition + yButtonIndent, buttonWidth, height / 40 * (textLines + 1), Component.literal(""), button -> {
                    button(index);
                });


                if (index > 0) {
                    if (textLines == 1 && ((quest.get(buttonIndex - 1).getValue().length() * 5) / maxLength) == 0) {
                        questSelect.y -= height / 40 * (textLines + 1);
                        yButtonPosition -= height / 40 * (textLines + 1);
                    }

                    if (textLines == 0 && ((quest.get(buttonIndex - 1).getValue().length() * 5) / maxLength) == 1) {
                        questSelect.y += buttonIndent / 2 * scale;
                        splitIndent += buttonIndent / 2 * scale;
                    }
                }

                splitIndent += buttonIndent / 2 * scale;


                addWidget(questSelect);
            }
        }


        Button prevPage = new Button((int) (xScreenPos - (imageWidth / 8.5)), (int) (yScreenPos + imageHeight * 0.69), width / 65, width / 65, Component.literal(""), button -> {
            if (selectedPage > 0) {
                selectedPage--;

                init();
            }
        });

        Button nextPage = new Button((int) (xScreenPos - (imageWidth / 17)), (int) (yScreenPos + imageHeight * 0.69), width / 65, width / 65, Component.literal(""), button -> {
            if (selectedPage + 1 < Math.ceil(quest.size()) / 4) {
                selectedPage++;

                init();
            }
        });

        addWidget(prevPage);
        addWidget(nextPage);
    }


    private void button(int index) {
        SendQuestPacket.TO_SERVER(new RequestQuestTracked(quest.get(index).getKey()));
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


    public void renderQuestTitles(PoseStack poseStack) {
        List<String> questList = new ArrayList<>();

        for (int i = 0; i < quest.size(); i++) {
            if (i < 4 && quest.size() > (i + (4 * (selectedPage)))) {
                questList.add(quest.get(i + (4 * (selectedPage))).getValue());
            }
        }

        MCUtil.renderText(poseStack, xScreenPos - (imageWidth / 2.4), yScreenPos * 1.6, 16, ((float) width) / 575, 23, questList, font);
    }

    public void renderQuestData(PoseStack poseStack) {
        list.clear();

        list.add("Type: " + trackedQuest_type);

        for (int i = 0; i < trackedQuest_target.size(); i++) {
            AbstractTargetType targetType = TemplateRegistry.getTargetType(EnumQuestType.valueOf(trackedQuest_type));

            if (targetType != null) {
                String translationKey = targetType.handler(new ResourceLocation(trackedQuest_target.get(i)));
                list.add(I18n.get(translationKey) + ": " + trackedTarget_current_quantity.get(i) + "/" + trackedTarget_quantity.get(i));
            } else {
                list.add(trackedQuest_target.get(i) + ": " + trackedTarget_current_quantity.get(i) + "/" + trackedTarget_quantity.get(i));
            }
        }

        if (trackedHasTimeLimit) list.add("Time limit: " + trackedTimeLimitInSeconds);


        MCUtil.renderText(poseStack, xScreenPos + (imageWidth / 20), yScreenPos * 1.6, 20, ((float) width) / 700, 28, list, font);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
