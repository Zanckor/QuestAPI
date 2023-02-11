package dev.zanckor.mod.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.zanckor.api.quest.abstracquest.AbstractTargetType;
import dev.zanckor.example.enumregistry.enumquest.EnumQuestType;
import dev.zanckor.api.quest.register.TemplateRegistry;
import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.network.SendQuestPacket;
import dev.zanckor.mod.network.message.screen.RequestQuestTracked;
import dev.zanckor.mod.util.MCUtil;
import dev.zanckor.mod.network.ClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class QuestListScreen extends Screen {
    private final static ResourceLocation QUEST_LOG = new ResourceLocation(QuestApiMain.MOD_ID, "textures/gui/questlog_api.png");
    List<String> questData = new ArrayList<>();
    int selectedPage = 0;


    double xScreenPos, yScreenPos;
    int imageWidth, imageHeight;
    EditBox textButton;
    String questSearch;

    HashMap<Integer, Map.Entry<String, String>> quest = new HashMap<>();

    public QuestListScreen(List<String> id, List<String> title) {
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

        HashMap<Integer, Integer> displayedButton = new HashMap<>();

        int xButtonPosition = (int) (xScreenPos - (imageWidth / 2.4));
        int yButtonPosition = (int) (yScreenPos * 1.6);

        for (int i = 0; i < quest.size(); i++) {
            if (questSearch != null && !questSearch.isEmpty()) {
                if (!quest.get(i).getValue().toLowerCase().contains(questSearch.toLowerCase())) {
                    continue;
                }
            }

            if (displayedButton.size() < 4 && quest.size() > (i + (4 * (selectedPage)))) {
                int buttonIndex = i + (4 * (selectedPage));
                int index = buttonIndex;

                int textLines = (quest.get(buttonIndex).getValue().length() * 5) / maxLength;
                int yButtonIndent = (int) (((8 * (textLines + 1)) * displayedButton.size() * scale) + splitIndent);

                int buttonWidth = textLines < 1 ? (int) (quest.get(buttonIndex).getValue().length() * 5 * scale) : (int) (maxLength * scale);

                Button questSelect = new Button(xButtonPosition, yButtonPosition + yButtonIndent, buttonWidth, height / 40 * (textLines + 1), Component.literal(""), button -> {
                    button(index);
                });


                if (displayedButton.get(displayedButton.size()) != null) {
                    int lastButton = displayedButton.get(displayedButton.size());


                    if (textLines == 1 && ((quest.get(lastButton).getValue().length() * 5) / maxLength) == 0) {
                        questSelect.y -= height / 40 * (textLines + 1);
                        yButtonPosition -= height / 40 * (textLines + 1);
                    }


                    if (textLines == 0 && ((quest.get(lastButton).getValue().length() * 5) / maxLength) == 1) {
                        questSelect.y += buttonIndent / 2 * scale;
                        splitIndent += buttonIndent / 2 * scale;
                    }
                }

                splitIndent += buttonIndent / 2 * scale;
                displayedButton.put(displayedButton.size() + 1, i);

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

        textButton = new EditBox(font,
                (int) (xScreenPos - (imageWidth / 2.4)), (int) (yScreenPos + imageHeight * 0.68),
                width / 8, 10,
                Component.literal(""));


        addRenderableWidget(textButton);

        textButton.setValue(textButton.getValue());

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

        MCUtil.renderText(poseStack, (xScreenPos - imageWidth / 2.9), yScreenPos * 1.4, 10, ((float) width) / 500, 28, "Quest Title", font);

        Minecraft.getInstance().getProfiler().pop();
        super.render(poseStack, x, y, partialTicks);
    }

    @Override
    public boolean keyReleased(int x, int y, int p_94717_) {
        if (textButton != null && x == 257 && y == 28) {
            questSearch = textButton.getValue();
            init();
        }

        return super.keyReleased(x, y, p_94717_);
    }

    public void renderQuestTitles(PoseStack poseStack) {
        List<String> questList = new ArrayList<>();
        int displayedButton = 0;

        if (ClientHandler.trackedID != null) {

            for (int i = 0; i < quest.size(); i++) {
                if (questSearch != null && !questSearch.isEmpty()) {
                    if (!quest.get(i).getValue().toLowerCase().contains(questSearch.toLowerCase())) {
                        continue;
                    }
                }

                if (displayedButton < 4 && quest.size() > (i + (4 * (selectedPage)))) {
                    questList.add(quest.get(i + (4 * (selectedPage))).getValue());
                }

                displayedButton++;
            }

            MCUtil.renderText(poseStack, xScreenPos - (imageWidth / 2.4), yScreenPos * 1.6, 16, ((float) width) / 575, 23, questList, font);

        }
    }

    public void renderQuestData(PoseStack poseStack) {
        questData.clear();

        if (ClientHandler.trackedID != null) {
            questData.add("Type: " + ClientHandler.trackedID.substring(0, 1).toUpperCase() + ClientHandler.trackedQuest_type.substring(1).toLowerCase());

            for (int i = 0; i < ClientHandler.trackedQuest_target.size(); i++) {
                AbstractTargetType targetType = TemplateRegistry.getTargetType(EnumQuestType.valueOf(ClientHandler.trackedQuest_type));

                if (targetType != null) {
                    String translationKey = targetType.handler(new ResourceLocation(ClientHandler.trackedQuest_target.get(i)));
                    questData.add(I18n.get(translationKey) + ": " + ClientHandler.trackedTarget_current_quantity.get(i) + "/" + ClientHandler.trackedTarget_quantity.get(i));
                } else {
                    questData.add(ClientHandler.trackedQuest_target.get(i) + ": " + ClientHandler.trackedTarget_current_quantity.get(i) + "/" + ClientHandler.trackedTarget_quantity.get(i));
                }
            }

            if (ClientHandler.trackedHasTimeLimit) questData.add("Time limit: " + ClientHandler.trackedTimeLimitInSeconds);


            MCUtil.renderText(poseStack, xScreenPos + (imageWidth / 20), yScreenPos * 1.6, 20, ((float) width) / 700, 28, questData, font);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
