package dev.zanckor.example.client.screen.dialog;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.zanckor.example.client.screen.button.TextButton;
import dev.zanckor.example.common.enumregistry.enumdialog.EnumDialogOption;
import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.client.screen.abstractscreen.AbstractDialog;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.dialogoption.AddQuest;
import dev.zanckor.mod.common.network.message.dialogoption.DialogRequestPacket;
import dev.zanckor.mod.common.network.message.dialogoption.DisplayDialog;
import dev.zanckor.mod.common.network.message.screen.OpenVanillaEntityScreen;
import dev.zanckor.mod.common.util.MCUtilClient;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.checkerframework.checker.units.qual.C;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MinimalistDialogScreen extends AbstractDialog {
    int dialogID;
    String text;
    int textDisplayDelay;
    int textDisplaySize;
    int optionSize;
    HashMap<Integer, List<Integer>> optionIntegers;
    HashMap<Integer, List<String>> optionStrings;

    double xScreenPos, yScreenPos, scale;
    int imageWidth, imageHeight;
    int xButtonPosition, yButtonPosition;
    Entity entity;
    UUID npcUUID;
    String resourceLocation;
    Item item;
    DisplayDialog.NpcType npcType;


    private final static ResourceLocation DIALOG = new ResourceLocation(QuestApiMain.MOD_ID, "textures/gui/minimalist_dialog_background.png");


    public MinimalistDialogScreen(Component component) {
        super(component);
    }


    @Override
    public Screen modifyScreen(int dialogID, String text, int optionSize, HashMap<Integer, List<Integer>> optionIntegers, HashMap<Integer, List<String>> optionStrings, UUID npcUUID, String resourceLocation, Item item, DisplayDialog.NpcType npcType) {
        this.dialogID = dialogID;
        this.text = I18n.get(text);
        this.optionSize = optionSize;
        this.optionIntegers = optionIntegers;
        this.optionStrings = optionStrings;
        this.npcType = npcType;

        switch (npcType) {
            case RESOURCE_LOCATION -> {
                EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(resourceLocation));
                entity = entityType.create(Minecraft.getInstance().level);
                this.resourceLocation = resourceLocation;
            }

            case UUID -> entity = MCUtilClient.getEntityByUUID(npcUUID);
            case ITEM -> this.item = item;
        }

        this.npcUUID = npcUUID;

        return this;
    }

    @Override
    protected void init() {
        super.init();

        textDisplaySize = 0;
        imageWidth = (int) (width / 1.35);
        imageHeight = width / 8;
        xScreenPos = imageWidth / 1.5;
        yScreenPos = (double) width / 3;
        scale = ((float) width) / 700;

        xButtonPosition = (int) (xScreenPos - (imageWidth / 2.75));
        yButtonPosition = (int) (yScreenPos * 1.225);

        for (int i = 0; i < optionSize; i++) {
            int stringLength = (int) ((optionStrings.get(i).get(0).length()) * (2.6 * scale));
            int index = i;

            if (xButtonPosition + stringLength > (width / 1.3)) {
                yButtonPosition += 18 * scale;
                xButtonPosition = (int) (xScreenPos - (imageWidth / 2.75));
            }

            addRenderableWidget(new TextButton(xButtonPosition, yButtonPosition, stringLength, 20, ((float) width) / 675,
                    Component.literal(I18n.get(optionStrings.get(i).get(0))).withStyle(ChatFormatting.WHITE), 26, button -> button(index)));

            xButtonPosition += (stringLength);
        }

        addRenderableWidget(new TextButton((int) (xScreenPos / 2 + (imageWidth / 1.35)), (int) (yScreenPos * 1.235), (int) (20 * scale), (int) (20 * scale), ((float) width) / 675,
                Component.literal("↩").withStyle(ChatFormatting.WHITE).withStyle(ChatFormatting.BOLD), 26, button -> {
            if (npcUUID != null) SendQuestPacket.TO_SERVER(new OpenVanillaEntityScreen(npcUUID));
        }));
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
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int xPosition = (width / 4);
        int yPosition = (int) (yScreenPos + yScreenPos / 16);
        PoseStack poseStack = graphics.pose();

        graphics.setColor(1, 1, 1, 0.5f);
        graphics.blit(DIALOG, (int) (xScreenPos - (imageWidth / 2)), (int) yScreenPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
        graphics.setColor(1, 1, 1, 1);

        MCUtilClient.renderLine(graphics, poseStack, 80, xPosition, yPosition, (float) width / 675, 16, Component.literal(text.substring(0, textDisplaySize)).withStyle(ChatFormatting.WHITE), font);

        switch (npcType) {
            case UUID, RESOURCE_LOCATION -> {
                if (entity != null) MCUtilClient.renderEntity(
                        xScreenPos / 2.7, yScreenPos * 1.325, (double) width / 24,
                        (xScreenPos / 2.7 - mouseX) / 4, (yScreenPos * 1.3 - mouseY) / 4,
                        (LivingEntity) entity);
            }

            case ITEM -> MCUtilClient.renderItem(item.getDefaultInstance(),
                    (int) (xScreenPos / 2.65), (int) (yScreenPos * 1.185), (double) width / 150,
                    0, poseStack);
        }

        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    private void button(int optionID) {
        EnumDialogOption optionType = EnumDialogOption.valueOf(optionStrings.get(optionID).get(1));

        switch (optionType) {
            case OPEN_DIALOG, CLOSE_DIALOG:
                SendQuestPacket.TO_SERVER(new DialogRequestPacket(optionType, optionID, entity, resourceLocation, item, npcType));
                break;
            case ADD_QUEST:
                SendQuestPacket.TO_SERVER(new AddQuest(optionType, optionID));
                break;
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
