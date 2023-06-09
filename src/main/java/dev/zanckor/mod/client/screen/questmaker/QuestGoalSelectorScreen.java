package dev.zanckor.mod.client.screen.questmaker;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractTargetType;
import dev.zanckor.api.filemanager.quest.codec.server.ServerGoal;
import dev.zanckor.api.filemanager.quest.codec.server.ServerQuest;
import dev.zanckor.api.filemanager.quest.register.QuestTemplateRegistry;
import dev.zanckor.example.client.screen.button.TextButton;
import dev.zanckor.example.common.enumregistry.EnumRegistry;
import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.common.util.MCUtilClient;
import dev.zanckor.mod.server.menu.questmaker.QuestMakerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.concurrent.atomic.AtomicInteger;

public class QuestGoalSelectorScreen extends AbstractContainerScreen<QuestMakerMenu> {
    private final static ResourceLocation BG = new ResourceLocation(QuestApiMain.MOD_ID, "textures/gui/quest_maker_bg.png");
    private int xPosition, yPosition;
    private float scale;
    ServerQuest editingQuest;

    public QuestGoalSelectorScreen(QuestMakerMenu questDefaultMenu, Inventory inventory, ServerQuest editingQuest, Component component) {
        super(questDefaultMenu, inventory, component);

        this.editingQuest = editingQuest;
    }

    @Override
    protected void init() {
        super.init();

        xPosition = width / 2;
        yPosition = height / 2;
        imageWidth = width / 2;
        imageHeight = width / 4;
        scale = ((float) width) / 750;

        //For each available goal to edit in quest, create a  MCUtilClient.createButton
        AtomicInteger buttonYOffSet = new AtomicInteger();
        for (int goalIndex = 0; goalIndex < editingQuest.getGoalList().size(); goalIndex++) {
            ServerGoal goal = editingQuest.getGoalList().get(goalIndex);
            Enum goalEnum = EnumRegistry.getEnum(goal.getType(), EnumRegistry.getQuestGoal());
            AbstractTargetType translatableTargetType = QuestTemplateRegistry.getTranslatableTargetType(goalEnum);

            String buttonText = goal.getType() + ": " + translatableTargetType.target(goal.getTarget());

            int lines = (int) Math.ceil(buttonText.length() / 14) + 1;
            int linesOffSet = lines * 10;

            TextButton textButton = new TextButton((int) (xPosition / 1.9), (int) (yPosition / 1.3) + buttonYOffSet.get(), 80, lines * 20, scale,
                    Component.literal(buttonText), 14,
                    onPress -> {
                    });

            buttonYOffSet.addAndGet((int) (Math.max(linesOffSet, 10) * scale));
            addRenderableWidget(textButton);
        }
    }

    @Override
    protected void renderBg(GuiGraphics p_283065_, float p_97788_, int p_97789_, int p_97790_) {

    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        PoseStack poseStack = graphics.pose();
        graphics.blit(BG, xPosition - (imageWidth / 2), yPosition - (imageHeight / 2), 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);

        poseStack.pushPose();
        renderTitles(graphics, poseStack, scale, xPosition, yPosition);
        poseStack.popPose();

        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    public void renderTitles(GuiGraphics graphics, PoseStack poseStack, float scale, int xPosition, int yPosition) {
        String questTitle = I18n.get(editingQuest.getTitle());

        MCUtilClient.renderText(graphics, poseStack, xPosition, yPosition / 1.5, 0, scale, 40, questTitle, font);
        MCUtilClient.renderText(graphics, poseStack, xPosition / 1.725, yPosition / 1.5, 0, scale, 40, "Goals", font);
    }

    @Override
    protected void renderLabels(GuiGraphics p_281635_, int p_282681_, int p_283686_) {
        super.renderLabels(p_281635_, p_282681_, p_283686_);
    }
}
