package dev.zanckor.mod.client.screen.questmaker;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.zanckor.api.filemanager.quest.codec.server.ServerQuest;
import dev.zanckor.example.client.screen.button.TextButton;
import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.common.util.MCUtilClient;
import dev.zanckor.mod.server.menu.questmaker.QuestMakerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.concurrent.atomic.AtomicInteger;

public class QuestDefaultScreen extends AbstractContainerScreen<QuestMakerMenu> {
    private final static ResourceLocation BG = new ResourceLocation(QuestApiMain.MOD_ID, "textures/gui/quest_maker_bg.png");
    QuestMakerMenu menu;
    Inventory inventory;
    private int xPosition, yPosition;
    private float scale;
    EditBox idBox, titleBox, timeLimitBox;
    ServerQuest editingQuest;

    public QuestDefaultScreen(QuestMakerMenu questDefaultMenu, Inventory inventory, Component component) {
        super(questDefaultMenu, inventory, component);

        this.menu = questDefaultMenu;
        this.inventory = inventory;
    }

    @Override
    protected void init() {
        super.init();

        xPosition = width / 2;
        yPosition = height / 2;
        imageWidth = width / 2;
        imageHeight = width / 4;
        scale = ((float) width) / 750;

        //Add buttons for different selection of data, quests, requirements...
        Button goalScreen =  MCUtilClient.createButton(xPosition, (int) (yPosition * 0.4), 40, 20, new TextComponent("Goals"),
                onPress -> {
                    if (editingQuest != null) {

                        onSaveButtonPress();
                        Minecraft.getInstance().setScreen(new QuestGoalSelectorScreen(menu, inventory, editingQuest, new TextComponent("quest_goal_selector")));
                    }
                });

        addRenderableWidget(goalScreen);

        //Add buttons for save and edit actual quest
        idBox = new EditBox(font, (int) (xPosition / 1.15), (int) (yPosition / 1.5), width / 90 * 20, width / 90, new TextComponent(""));
        titleBox = new EditBox(font, (int) (xPosition / 1.15), (int) (yPosition / 1.3), width / 90 * 20, width / 90, new TextComponent(""));
        timeLimitBox = new EditBox(font, (int) (xPosition / 1.07), (int) (yPosition / 1.1), width / 90 * 17, width / 90, new TextComponent(""));
        addRenderableWidget(idBox);
        addRenderableWidget(titleBox);
        addRenderableWidget(timeLimitBox);

        TextButton saveButton = new TextButton((int) (xPosition * 1.35), (int) (yPosition * 1.3), 20, 20, scale,
                new TextComponent("SAVE"), 14,
                onPress -> onSaveButtonPress());

        addRenderableWidget(saveButton);


        //For each available quest to edit in serverQuest folder, create a  MCUtilClient.createButton
        //On click in that button, adds that quest to a list of editing quests on QuestMakerManager to save the data
        AtomicInteger buttonYOffSet = new AtomicInteger();
        for (int questIndex = 0; questIndex < QuestMakerManager.availableQuests.size(); questIndex++) {
            ServerQuest quest = QuestMakerManager.availableQuests.get(questIndex);

            String questTitle = quest.getTitle();
            questTitle = questTitle.startsWith("#") ? I18n.get("quest_name.questapi." + quest.getTitle().substring(1)) : questTitle;

            int lines = (int) Math.max(1, Math.ceil(questTitle.length() / 14));
            int linesOffSet = lines * 20;

            TextButton textButton = new TextButton((int) (xPosition / 1.9), (int) (yPosition / 1.3) + buttonYOffSet.get(), 80, lines * 20, scale,
                    new TextComponent(questTitle), 14,
                    onPress -> {
                        onQuestButtonPress(quest);
                    });

            buttonYOffSet.addAndGet((int) (linesOffSet * scale));
            addRenderableWidget(textButton);
        }
    }


    public void onQuestButtonPress(ServerQuest quest) {
        QuestMakerManager.addQuest(quest);
        editingQuest = quest;

        idBox.setValue(quest.getId());
        titleBox.setValue(quest.getTitle());
        timeLimitBox.setValue(String.valueOf(quest.getTimeLimitInSeconds()));
    }

    public void onSaveButtonPress() {
        editingQuest.setId(idBox.getValue());
        editingQuest.setTitle(titleBox.getValue());

        editingQuest.setHasTimeLimit(Integer.parseInt(timeLimitBox.getValue()) > 0);
        editingQuest.setTimeLimitInSeconds(Integer.parseInt(timeLimitBox.getValue()));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        poseStack.pushPose();
        RenderSystem.setShaderTexture(0, BG);

        blit(poseStack, xPosition - (imageWidth / 2), yPosition - (imageHeight / 2), 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
        renderTitles(poseStack, scale, xPosition, yPosition);

        poseStack.popPose();

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderLabels(PoseStack p_97808_, int p_97809_, int p_97810_) {
    }

    public void renderTitles(PoseStack poseStack, float scale, int xPosition, int yPosition) {
        MCUtilClient.renderText(poseStack, xPosition / 1.75, yPosition / 1.5, 0, scale, 40, "Quests", font);

        MCUtilClient.renderText(poseStack, xPosition / 1.3, yPosition / 1.5, 0, scale, 40, "ID:", font);
        MCUtilClient.renderText(poseStack, xPosition / 1.3, yPosition / 1.3, 0, scale, 40, "TITLE:", font);

        MCUtilClient.renderText(poseStack, xPosition / 1.3, yPosition / 1.1, 0, scale, 40, "TIME LIMIT:", font);
    }


    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {

    }
}
