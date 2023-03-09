package dev.zanckor.example.client.screen.questlog;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.zanckor.api.filemanager.quest.UserQuest;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractTargetType;
import dev.zanckor.api.filemanager.quest.register.TemplateRegistry;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestType;
import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.client.screen.AbstractQuestLog;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.screen.RequestQuestTracked;
import dev.zanckor.mod.common.util.MCUtil;
import dev.zanckor.mod.common.util.MCUtilClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

import static dev.zanckor.mod.common.network.handler.ClientHandler.*;
import static dev.zanckor.mod.common.util.MCUtilClient.properNoun;

public class QuestLog extends AbstractQuestLog {
    private final static ResourceLocation QUEST_LOG = new ResourceLocation(QuestApiMain.MOD_ID, "textures/gui/questlog_api.png");
    int selectedPage = 0;


    double xScreenPos, yScreenPos;
    int imageWidth, imageHeight;
    EditBox textButton;
    String questSearch;

    HashMap<Integer, Map.Entry<String, String>> quest = new HashMap<>();

    public QuestLog(Component component) {
        super(component);
    }

    @Override
    public Screen modifyScreen(List<String> id, List<String> title) {
        quest.clear();

        for (int i = 0; i < title.size(); i++) {
            quest.put(i, new AbstractMap.SimpleEntry<>(id.get(i), title.get(i)));
        }

        return this;
    }

    @Override
    protected void init() {
        super.init();
        clearWidgets();

        imageWidth = width / 2;
        imageHeight = width / 3;
        xScreenPos = width - (imageWidth);
        yScreenPos = (double) height / 4;

        float scale = ((float) width) / 575;
        int buttonIndent = 16;
        int maxLength = 22 * 5;
        float splitIndent = 0;
        int xButtonPosition = (int) (xScreenPos - (imageWidth / 2.4));
        int yButtonPosition = (int) (yScreenPos * 1.7);

        HashMap<Integer, Integer> displayedButton = new HashMap<>();


        for (int i = 0; i < quest.size(); i++) {
            if (questSearch != null && !questSearch.isEmpty() && !quest.get(i).getValue().toLowerCase().contains(questSearch.toLowerCase()))
                continue;

            if (displayedButton.size() < 4 && quest.size() > (i + (4 * selectedPage))) {
                int buttonIndex = i + (4 * selectedPage);
                int textLines = (quest.get(buttonIndex).getValue().length() * 5) / maxLength;
                int yButtonIndent = (int) (((8 * (textLines + 1)) * displayedButton.size() * scale) + splitIndent);
                int buttonWidth = textLines < 1 ? (int) (quest.get(buttonIndex).getValue().length() * 5 * scale) : (int) (maxLength * scale);

                Button questSelect = new Button(xButtonPosition, yButtonPosition + yButtonIndent, buttonWidth, height / 40 * (textLines + 1), Component.literal(""), button -> SendQuestPacket.TO_SERVER(new RequestQuestTracked(quest.get(buttonIndex).getKey())));

                if (displayedButton.size() > 0) {
                    int prevButton = displayedButton.get(displayedButton.size());

                    if (textLines == 1 && ((quest.get(prevButton).getValue().length() * 5) / maxLength) == 0) {
                        questSelect.y -= height / 80 * (textLines + 1);
                        yButtonPosition -= (height / 80) / 8 * (textLines + 1);
                    }

                    if (textLines == 0 && ((quest.get(prevButton).getValue().length() * 5) / maxLength) == 1) {
                        questSelect.y += (buttonIndent / 2) * scale;
                        splitIndent += (buttonIndent / 2) * scale;
                    }
                }

                splitIndent += buttonIndent / 2 * scale;
                displayedButton.put(displayedButton.size() + 1, i);

                addWidget(questSelect);
            }
        }


        Button prevPage = new Button((int) (xScreenPos - (imageWidth / 5.5)), (int) (yScreenPos + imageHeight * 0.85), width / 25, width / 30, Component.literal(""), button -> {
            if (selectedPage > 0) {
                selectedPage--;

                init();
            }
        });
        Button nextPage = new Button((int) (xScreenPos - (imageWidth / 9)), (int) (yScreenPos + imageHeight * 0.85), width / 25, width / 30, Component.literal(""), button -> {
            if (selectedPage + 1 < Math.ceil(quest.size()) / 4) {
                selectedPage++;

                init();
            }
        });
        textButton = new EditBox(font, (int) (xScreenPos - (imageWidth / 2.4)), (int) (yScreenPos + imageHeight * 0.75), width / 6, 10, Component.literal(""));

        addRenderableWidget(textButton);
        addWidget(prevPage);
        addWidget(nextPage);

        textButton.setValue("Quest name");
    }

    @Override
    public boolean keyReleased(int x, int y, int p_94717_) {
        if (textButton != null && x == 257 && y == 28) {
            questSearch = textButton.getValue();
            init();
        }

        return super.keyReleased(x, y, p_94717_);
    }

    @Override
    public boolean mouseClicked(double x, double y, int p_94697_) {
        textButton.setValue("");

        return super.mouseClicked(x, y, p_94697_);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int x, int y, float partialTicks) {
        float scale = ((float) width) / 500;

        Minecraft.getInstance().getProfiler().push("background");
        RenderSystem.setShaderTexture(0, QUEST_LOG);
        blit(poseStack, (int) (xScreenPos - (imageWidth / 2)), (int) yScreenPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);

        poseStack.pushPose();
        renderQuestTitles(poseStack);
        renderQuestData(poseStack);

        MCUtilClient.renderText(poseStack, (float) (xScreenPos - imageWidth / 2.9), (float) (yScreenPos * 1.35), 0, scale, 40, "Quest Title", font);
        MCUtilClient.renderText(poseStack, (float) (xScreenPos + imageWidth * 0.135), (float) (yScreenPos * 1.35), 0, scale, 40, "Quest Info", font);

        poseStack.popPose();

        Minecraft.getInstance().getProfiler().pop();
        super.render(poseStack, x, y, partialTicks);
    }

    public void renderQuestTitles(PoseStack poseStack) {
        List<String> questList = new ArrayList<>();
        int displayedButton = 0;

        if (quest != null) {

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


            MCUtilClient.renderText(poseStack, xScreenPos - (imageWidth / 2.4), yScreenPos * 1.7, 16, ((float) width) / 575, 23, questList, font);

        }
    }

    public void renderQuestData(PoseStack poseStack) {
        //TODO: Make multiple pages if quest has a lot of data
        try {
            if (userQuest == null || MCUtil.isQuestCompleted(userQuest)) return;

            HashMap<String, List<UserQuest.QuestGoal>> userQuests = new HashMap<>();
            int xPosition = (int) (width / 1.925);
            int yPosition = (int) (width / 4.25);
            float scale = ((float) width) / 700;

            poseStack.pushPose();
            poseStack.translate(xPosition, yPosition, 0);
            poseStack.scale(scale, scale, 0);


            //Gets all quest types on json and creates a HashMap with a list of goals
            for (UserQuest.QuestGoal questGoal : userQuest.getQuestGoals()) {
                String type = questGoal.getType();
                List<UserQuest.QuestGoal> questGoalList = userQuests.get(type);


                if (questGoalList == null) {
                    questGoalList = new ArrayList<>();
                }

                questGoalList.add(questGoal);

                userQuests.put(type, questGoalList);
            }

            //Displays quest goals
            MCUtilClient.renderLines(poseStack, 20, 30, "Quest: " + properNoun(questTitle), font);

            for (Map.Entry<String, List<UserQuest.QuestGoal>> entry : userQuests.entrySet()) {
                List<UserQuest.QuestGoal> questGoalList = entry.getValue();
                MCUtilClient.renderLines(poseStack, 10, 30, "Quest Type: " + properNoun(questGoalList.get(0).getType()), font);

                for (UserQuest.QuestGoal questGoal : questGoalList) {
                    AbstractTargetType targetType = TemplateRegistry.getTargetType(EnumQuestType.valueOf(questGoal.getType()));
                    String translationKey = questGoal.getTarget();
                    if (targetType != null)
                        translationKey = targetType.handler(new ResourceLocation(questGoal.getTarget()));

                    MCUtilClient.renderLines(poseStack, 10, 30, properNoun(I18n.get(translationKey)) + ": " + questGoal.getCurrentAmount() + "/" + questGoal.getAmount(), font);
                }


                poseStack.translate(0, 10, 0);
            }


            if (questHasTimeLimit) {
                MCUtilClient.renderLines(poseStack, 10, 30, "Time limit: " + questTimeLimit, font);
            }


            poseStack.popPose();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
