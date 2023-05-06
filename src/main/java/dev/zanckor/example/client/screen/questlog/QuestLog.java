package dev.zanckor.example.client.screen.questlog;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractTargetType;
import dev.zanckor.api.filemanager.quest.codec.user.UserGoal;
import dev.zanckor.api.filemanager.quest.codec.user.UserQuest;
import dev.zanckor.api.filemanager.quest.register.QuestTemplateRegistry;
import dev.zanckor.example.client.screen.button.TextButton;
import dev.zanckor.example.common.enumregistry.EnumRegistry;
import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.client.screen.abstractscreen.AbstractQuestLog;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.screen.RequestQuestTracked;
import dev.zanckor.mod.common.util.GsonManager;
import dev.zanckor.mod.common.util.MCUtil;
import dev.zanckor.mod.common.util.MCUtilClient;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.zanckor.mod.common.network.handler.ClientHandler.*;

public class QuestLog extends AbstractQuestLog {
    private final static ResourceLocation QUEST_LOG = new ResourceLocation(QuestApiMain.MOD_ID, "textures/gui/questlog_api.png");
    int selectedPage = 0;


    double xScreenPos, yScreenPos;
    int imageWidth, imageHeight;
    EditBox textButton;
    String questSearch;
    int questInfoScroll;
    float sin;


    List<UserQuest> questList = new ArrayList<>();

    public QuestLog(Component component) {
        super(component);
    }

    @Override
    public Screen modifyScreen() throws IOException {
        //Each time screen opens hashMap is deleted and questList
        questList.clear();

        for (int i = 0; i < activeQuestList.size(); i++) {
            UserQuest quest = (UserQuest) GsonManager.getJsonClass(activeQuestList.get(i), UserQuest.class);

            questList.add(quest);
        }


        return this;
    }

    @Override
    protected void init() {
        super.init();
        clearWidgets();

        questInfoScroll = 0;
        imageWidth = width / 2;
        imageHeight = width / 3;
        xScreenPos = width - (imageWidth);
        yScreenPos = (double) height / 4;
        int xButtonPosition = (int) (xScreenPos - (imageWidth / 2.25));
        int yButtonPosition = (int) (yScreenPos + ((((float) width) / 500) * 40));

        float textLengthScale = ((float) width) / 575;
        float buttonScale = ((float) width) / 700;
        int maxLength = 26 * 5;

        HashMap<Integer, Integer> displayedButton = new HashMap<>();

        //For each quest, checks if it can be added as a button to display data
        for (int i = 0; i < activeQuestList.size(); i++) {
            int buttonIndex = i + (4 * selectedPage);

            //Only add widget if there's 4 or fewer buttons added
            if (displayedButton.size() < 4 && questList.size() > buttonIndex) {
                String title = (questList.get(buttonIndex).getTitle());
                if (title.startsWith("#")) {
                    title = I18n.get("quest_name.questapi." + questList.get(i + (4 * (selectedPage))).getTitle().substring(1));
                }


                int textLines = (title.length() * 5) / maxLength;
                float buttonWidth = textLines < 1 ? questList.get(buttonIndex).getTitle().length() * 5 * textLengthScale : maxLength * textLengthScale;

                Button questSelect = new TextButton(
                        xButtonPosition, yButtonPosition, (int) buttonWidth, 20 * (textLines + 1), buttonScale,

                        Component.literal(title), 26,
                        button -> SendQuestPacket.TO_SERVER(new RequestQuestTracked(questList.get(buttonIndex).getId())));

                //If is not first button added, checks if prevButton has 1 or more lines to add an extra indent
                if (displayedButton.size() > 0) {
                    int prevButton = displayedButton.get(displayedButton.size());
                    String prevButtonTitle = (questList.get(buttonIndex).getTitle());
                    if (prevButtonTitle.startsWith("#")) {
                        prevButtonTitle = I18n.get("quest_name.questapi." + questList.get(prevButton).getTitle().substring(1));
                    }
                    int prevButtonLines = (prevButtonTitle.length() * 5) / maxLength;


                    if (prevButtonLines > 0) {
                        questSelect.setY((int) (questSelect.getY() + 13 * (prevButtonLines) * buttonScale));
                    }
                }

                displayedButton.put(displayedButton.size() + 1, i);

                questSelect.setY((int) (questSelect.getY() + buttonIndex * 27 * buttonScale));
                addRenderableWidget(questSelect);
            }
        }

        Button prevPage =  MCUtilClient.createButton((int) (xScreenPos - (imageWidth / 5.5)), (int) (yScreenPos + imageHeight * 0.85), width / 25, width / 30, Component.literal(""), button -> {
            if (selectedPage > 0) {
                selectedPage--;

                init();
            }
        });
        
        
        Button nextPage =  MCUtilClient.createButton((int) (xScreenPos - (imageWidth / 9)), (int) (yScreenPos + imageHeight * 0.85), width / 25, width / 30, Component.literal(""), button -> {
            if (selectedPage + 1 < Math.ceil(questList.size()) / 4) {
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
        renderQuestData(poseStack);

        MCUtilClient.renderText(poseStack, (float) (xScreenPos - imageWidth / 2.9), (float) (yScreenPos + (20 * scale)), 0, scale, 40, "Quest Title", font);
        MCUtilClient.renderText(poseStack, (float) (xScreenPos + imageWidth * 0.135), (float) (yScreenPos + (20 * scale)), 0, scale, 40, "Quest Info", font);

        poseStack.popPose();
        Minecraft.getInstance().getProfiler().pop();

        super.render(poseStack, x, y, partialTicks);
    }

    public void renderQuestData(PoseStack poseStack) {
        if (userQuest == null || userQuest.isCompleted())
            return;

        HashMap<String, List<UserGoal>> userQuestHashMap = new HashMap<>();
        int xPosition = (int) (width / 1.925);
        float scale = ((float) width) / 700;

        poseStack.pushPose();
        poseStack.translate(xPosition, yScreenPos + ((((float) width) / 500) * 40), 0);
        poseStack.scale(scale, scale, 0);

        //Gets all quest types on json and creates a HashMap with a list of goals
        for (UserGoal questGoal : userQuest.getQuestGoals()) {
            String type = questGoal.getType();
            List<UserGoal> questGoalList = userQuestHashMap.get(type);


            if (questGoalList == null) {
                questGoalList = new ArrayList<>();
            }

            questGoalList.add(questGoal);

            userQuestHashMap.put(type, questGoalList);
        }

        //Displays quest goals
        renderTitle(poseStack, minecraft);
        renderQuestType(poseStack, minecraft, userQuestHashMap);

        if (questHasTimeLimit) {
            MCUtilClient.renderLine(poseStack, 0, 0, 30, I18n.get("tracker.questapi.time_limit") + questTimeLimit, font);
        }


        poseStack.popPose();
    }

    public void renderTitle(PoseStack poseStack, Minecraft minecraft) {
        //If quest title on quest.json starts with # means that is a translatable string, else is rendered literally
        String title = questTitle;
        if (questTitle.startsWith("#")) {
            title = I18n.get("quest_name.questapi." + questTitle.substring(1));
        }

        MCUtilClient.renderLines(poseStack, 25, 10, 30, I18n.get("tracker.questapi.quest") + title, minecraft.font);
    }

    public void renderQuestType(PoseStack poseStack, Minecraft minecraft, HashMap<String, List<UserGoal>> userQuestHashMap) {
        int scissorBottom = (int) (height - (yScreenPos + imageHeight) + 15) * 2;
        int scissorTop = (int) (imageHeight * 1.25) - 15;

        RenderSystem.enableScissor(width, scissorBottom, (width / 2) + (imageWidth / 2), scissorTop);

        Font font = minecraft.font;
        Player player = minecraft.player;
        poseStack.translate(0, questInfoScroll + 20, 0);
        sin += 0.5;

        for (Map.Entry<String, List<UserGoal>> entry : userQuestHashMap.entrySet()) {
            List<UserGoal> questGoalList = entry.getValue();

            //Render quest type
            MCUtilClient.renderLine(poseStack, 30, 0, 0, 10, Component.literal(I18n.get("tracker.questapi.quest_type") +
                    I18n.get("quest_type.questapi." + questGoalList.get(0).getType().toLowerCase())).withStyle(ChatFormatting.BLACK), font);

            //Render each quest goal of a single type and render target
            for (UserGoal questGoal : questGoalList) {
                Enum goalEnum = EnumRegistry.getEnum("TARGET_TYPE_" + questGoal.getType(), EnumRegistry.getTargetType());

                AbstractTargetType translatableTargetType = QuestTemplateRegistry.getTranslatableTargetType(goalEnum);
                MutableComponent goalComponentTarget = translatableTargetType.handler(questGoal.getTarget(), questGoal, player, ChatFormatting.GRAY, ChatFormatting.BLACK);

                translatableTargetType.renderTarget(poseStack, (goalComponentTarget.getString().length() * 6 - 4), 3, 0.7, Math.sin(sin), questGoal, questGoal.getTarget());
                MCUtilClient.renderLine(poseStack, 30, 0, 0, 10, goalComponentTarget.withStyle(ChatFormatting.ITALIC), font);
            }

            poseStack.translate(0, 10, 0);
        }

        RenderSystem.disableScissor();
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        boolean mouseXInBox = (mouseX > xScreenPos) && (mouseX < (xScreenPos + imageWidth / 2));
        boolean mouseYInBox = (mouseY > yScreenPos) && (mouseY < (yScreenPos + imageHeight));

        if (mouseXInBox && mouseYInBox) {
            questInfoScroll += scroll * 4;
        }

        return super.mouseScrolled(mouseX, mouseY, scroll);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
