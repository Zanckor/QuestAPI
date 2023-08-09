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
import dev.zanckor.mod.common.network.handler.ClientHandler;
import dev.zanckor.mod.common.network.message.screen.RequestActiveQuests;
import dev.zanckor.mod.common.util.MCUtilClient;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import static dev.zanckor.mod.common.network.handler.ClientHandler.activeQuestList;
import static dev.zanckor.mod.common.network.handler.ClientHandler.trackedQuestList;

public class QuestLog extends AbstractQuestLog {
    private final static ResourceLocation QUEST_LOG = new ResourceLocation(QuestApiMain.MOD_ID, "textures/gui/questlog_api.png");
    private final static ResourceLocation TRACK_BUTTON = new ResourceLocation(QuestApiMain.MOD_ID, "textures/gui/track_button.png");
    int selectedQuestPage = 0;
    int selectedInfoPage = 0;


    double xScreenPos, yScreenPos;
    int imageWidth, imageHeight;
    int questInfoScroll;
    float sin;

    UserQuest selectedQuest;

    public QuestLog(Component component) {
        super(component);
    }

    @Override
    public Screen modifyScreen() {
        init();
        return this;
    }

    @Override
    protected void init() {
        super.init();
        clearWidgets();
        selectedQuest = null;

        questInfoScroll = 0;
        imageWidth = width / 2;
        imageHeight = width / 3;
        xScreenPos = width - (imageWidth);
        yScreenPos = (double) height / 4;
        int xButtonPosition = (int) (xScreenPos - (imageWidth / 2.6));
        int yButtonPosition = (int) (yScreenPos + ((((float) width) / 500) * 40));

        float buttonScale = ((float) width) / 690;
        int maxLength = 26 * 5;

        HashMap<Integer, Integer> displayedButton = new HashMap<>();


        //For each quest, checks if it can be added as a button to display data
        //Only add widget if there's 4 or fewer buttons added
        IntStream.range(0, activeQuestList.size()).forEachOrdered(index -> {
            int buttonIndex = index + (4 * selectedQuestPage);
            if (displayedButton.size() < 4 && activeQuestList.size() > buttonIndex) {
                String title = I18n.get(activeQuestList.get(buttonIndex).getTitle());
                UserQuest currentQuest = activeQuestList.get(buttonIndex);
                AtomicBoolean containsSelectedQuest = new AtomicBoolean(false);

                //If trackedQuestList don't contain this quest, track it, else unTrack it.
                for (UserQuest trackedQuest : trackedQuestList) {
                    if (trackedQuest != null && trackedQuest.getId().equals(currentQuest.getId())) {
                        containsSelectedQuest.set(true);
                    }
                }

                int yUVOffset = containsSelectedQuest.get() ? (int) (14 * buttonScale) : 0;
                int textLines = (title.length() * 5) / maxLength;
                float buttonWidth = textLines < 1 ? (title.length() * 5) * buttonScale : maxLength * buttonScale;

                //Button to select a quest - Will change page and display quest data
                Button questSelect = new TextButton(
                        xButtonPosition, yButtonPosition, (int) buttonWidth, 20, buttonScale,
                        Component.literal(title), 26, button -> {
                    selectedQuest = currentQuest;
                    SendQuestPacket.TO_SERVER(new RequestActiveQuests());
                });

                //Button to track quest
                ImageButton trackQuest = new ImageButton(
                        (int) (xButtonPosition * 0.925), (yButtonPosition + 25),
                        width / 50, width / 50, 0, yUVOffset, 0,
                        TRACK_BUTTON,
                        width / 50, width / 25,
                        button -> {
                            ClientHandler.modifyTrackedQuests(!containsSelectedQuest.get(), currentQuest);
                            init();
                        });

                displayedButton.put(displayedButton.size() + 1, index);

                //Move 30 px down for render next button. Similar to split indent.
                questSelect.setY(((int) (questSelect.getY() + buttonIndex * (30 * buttonScale))));
                trackQuest.setY((int) (questSelect.getY() + buttonIndex * (30 * buttonScale)));

                addRenderableWidget(trackQuest);
                addRenderableWidget(questSelect);
            }
        });


        Button questPreviousPage = MCUtilClient.createButton((int) (xScreenPos - (imageWidth / 2.25)), (int) (yScreenPos + imageHeight * 0.81), width / 50, width / 50, Component.nullToEmpty(""), button -> {
            if (selectedQuestPage > 0) {
                selectedQuestPage--;

                init();
            }
        });
        Button questNextPage = MCUtilClient.createButton((int) (xScreenPos + (imageWidth / 2.5)), (int) (yScreenPos + imageHeight * 0.81), width / 50, width / 50, Component.nullToEmpty(""), button -> {
            if (selectedQuestPage + 1 < Math.ceil(activeQuestList.size()) / 4) {
                selectedQuestPage++;

                init();
            }
        });

        addWidget(questPreviousPage);
        addWidget(questNextPage);

        SendQuestPacket.TO_SERVER(new RequestActiveQuests());
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
        if (selectedQuest == null || selectedQuest.isCompleted()) return;

        HashMap<String, List<UserGoal>> userQuestHashMap = new HashMap<>();
        int xPosition = (int) (width / 1.925);
        float scale = ((float) width) / 700;

        poseStack.pushPose();
        poseStack.translate(xPosition, yScreenPos + ((((float) width) / 500) * 40), 0);
        poseStack.scale(scale, scale, 0);

        //Gets all quest types on json and creates a HashMap with a list of goals
        for (UserGoal questGoal : selectedQuest.getQuestGoals()) {
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
        renderGoals(poseStack, minecraft, userQuestHashMap);

        if (selectedQuest.hasTimeLimit()) {
            MCUtilClient.renderLine(poseStack, 0, 0, 30, I18n.get("tracker.questapi.time_limit") + selectedQuest.getTimeLimitInSeconds(), font);
        }


        poseStack.popPose();
    }

    public void renderTitle(PoseStack poseStack, Minecraft minecraft) {
        String title = I18n.get(selectedQuest.getTitle());
        MCUtilClient.renderLines(poseStack, 25, 10, 30, I18n.get("tracker.questapi.quest") + title, minecraft.font);
    }

    public void renderGoals(PoseStack poseStack, Minecraft minecraft, HashMap<String, List<UserGoal>> userQuestHashMap) {
        int scissorBottom = (int) (height * 0.4);
        int scissorTop = (int) (height * 0.7);

        RenderSystem.enableScissor(width, scissorBottom, (width / 2) + (imageWidth / 2), scissorTop);

        Font font = minecraft.font;
        Player player = minecraft.player;
        poseStack.translate(0, questInfoScroll + 10, 0);
        sin += 0.5;

        for (Map.Entry<String, List<UserGoal>> entry : userQuestHashMap.entrySet()) {
            List<UserGoal> questGoalList = entry.getValue();

            //Render quest type
            MCUtilClient.renderLine(poseStack, 30, 0, 0, 10, Component.literal(I18n.get("tracker.questapi.quest_type") +
                    I18n.get("quest_type." + questGoalList.get(0).getTranslatableType().toLowerCase())).withStyle(ChatFormatting.BLACK), font);

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

        renderDescription(poseStack);

        RenderSystem.disableScissor();
    }


    public void renderDescription(PoseStack poseStack) {
        if (selectedQuest.getDescription() != null) {
            MCUtilClient.renderLine(poseStack, 30, 30, 0, 18, "§l§nDESCRIPTION", font);
            poseStack.translate(-8, 0, 0);
            MCUtilClient.renderLine(poseStack, 30, 0, 0, 18, selectedQuest.getDescription(), font);
        }
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        boolean mouseXInBox = (mouseX > xScreenPos) && (mouseX < (xScreenPos + imageWidth / 2));
        boolean mouseYInBox = (mouseY > yScreenPos) && (mouseY < (yScreenPos + imageHeight));

        if (mouseXInBox && mouseYInBox) {
            questInfoScroll += scroll * 4;

            if (questInfoScroll > 0) questInfoScroll = 0;
        }

        return super.mouseScrolled(mouseX, mouseY, scroll);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
