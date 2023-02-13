package dev.zanckor.mod.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.zanckor.example.common.enumregistry.enumdialog.EnumOptionType;
import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.dialogoption.AddQuest;
import dev.zanckor.mod.common.network.message.dialogoption.DialogRequestPacket;
import dev.zanckor.mod.common.util.MCUtilClient;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;

import java.util.HashMap;
import java.util.List;

public class DialogScreen extends Screen {
    int dialogID;
    String text;
    int textDisplayDelay;
    int textDisplaySize;
    int optionSize;
    HashMap<Integer, List<Integer>> optionIntegers;
    HashMap<Integer, List<String>> optionStrings;

    double xScreenPos, yScreenPos;
    int imageWidth, imageHeight;


    private final static ResourceLocation DIALOG = new ResourceLocation(QuestApiMain.MOD_ID, "textures/gui/dialog_background.png");


    public DialogScreen(int dialogID, String text, int optionSize, HashMap<Integer, List<Integer>> optionIntegers, HashMap<Integer, List<String>> optionStrings) {
        super(Component.literal(String.valueOf(dialogID)));
        this.dialogID = dialogID;
        this.text = text;
        this.optionSize = optionSize;
        this.optionIntegers = optionIntegers;
        this.optionStrings = optionStrings;
    }


    @Override
    protected void init() {
        super.init();

        int xPosition = width / 4;
        int yPosition = (int) (height / 1.2);

        for (int i = 0; i < optionSize; i++) {
            int stringLength = optionStrings.get(i).get(0).length() * 5;

            int index = i;
            addRenderableWidget(new Button(xPosition, yPosition, stringLength, 20,
                    Component.literal(optionStrings.get(i).get(0)), button -> button(index, dialogID)));

            yPosition += 22;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (textDisplaySize < text.length()) {
            if (textDisplayDelay == 0) {
                MCUtilClient.playSound(SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON, 0.975f, 1.025f);
                textDisplaySize++;

                if (textDisplaySize < text.length()) {
                    switch (Character.toString(text.charAt(textDisplaySize))) {
                        case ".", "?", "!" -> textDisplayDelay = 9;
                        case "," -> textDisplayDelay = 5;
                    }
                }
            } else {
                textDisplayDelay--;
            }
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        poseStack.pushPose();
        RenderSystem.setShaderTexture(0, DIALOG);
        imageWidth = width / 2;
        imageHeight = width / 4;
        xScreenPos = width - (imageWidth);
        yScreenPos = (double) height / 4;

        blit(poseStack, (int) (xScreenPos - (imageWidth / 2)), (int) yScreenPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);


        int yPosition = (int) (height / 1.5);

        for (List<FormattedCharSequence> textBlock : MCUtilClient.splitText(text.substring(0, textDisplaySize), font, (int) (width / 1.9))) {
            for (FormattedCharSequence line : textBlock) {
                font.draw(poseStack, line, (float) (width / 4), yPosition, 0);

                yPosition += 14;
            }
        }

        poseStack.popPose();


        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    private void button(int optionID, int dialogID) {
        EnumOptionType optionType = EnumOptionType.valueOf(optionStrings.get(optionID).get(1));


        switch (optionType) {
            case OPEN_DIALOG, CLOSE_DIALOG -> SendQuestPacket.TO_SERVER(new DialogRequestPacket(optionType, optionID));
            case ADD_QUEST -> SendQuestPacket.TO_SERVER(new AddQuest(optionType, dialogID, optionID));
            case REMOVE_QUEST -> //TODO:
            System.out.println("A");
        }
    }

    @Override
    public boolean mouseClicked(double xPosition, double yPosition, int button) {
        textDisplaySize = text.length();

        return super.mouseClicked(xPosition, yPosition, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
