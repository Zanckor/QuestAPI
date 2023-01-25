package com.zanckor.mod.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zanckor.api.dialog.enumdialog.EnumOptionType;
import com.zanckor.mod.network.SendQuestPacket;
import com.zanckor.mod.network.message.dialogoption.DialogRequestPacket;
import com.zanckor.mod.network.message.dialogoption.AddQuest;
import com.zanckor.mod.util.MCUtil;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
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
                    Component.literal(optionStrings.get(i).get(0)), button -> {
                button(index, dialogID);
            }));

            yPosition += 22;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (textDisplaySize < text.length()) {
            if (textDisplayDelay == 0) {
                MCUtil.playTextSound();
                textDisplaySize++;

                if (textDisplaySize < text.length()) {
                    switch (Character.toString(text.charAt(textDisplaySize))) {
                        case ".", "?", "!" -> {
                            textDisplayDelay = 9;

                            break;
                        }

                        case "," -> {
                            textDisplayDelay = 5;
                            break;
                        }
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
        int yPosition = (int) (height / 1.5);

        for (List<FormattedCharSequence> textBlock : MCUtil.splitText(text.substring(0, textDisplaySize), font, (int) (width / 1.9))) {
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
            case OPEN_DIALOG, CLOSE_DIALOG -> {
                SendQuestPacket.TO_SERVER(new DialogRequestPacket(optionType, dialogID, optionID));
                break;
            }

            case ADD_QUEST -> {
                SendQuestPacket.TO_SERVER(new AddQuest(optionType, dialogID, optionID));
                break;
            }

            case REMOVE_QUEST -> {
                break;
            }
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