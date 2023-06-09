package dev.zanckor.example.common.handler.targettype;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractTargetType;
import dev.zanckor.api.filemanager.quest.codec.user.UserGoal;
import dev.zanckor.mod.common.util.MCUtilClient;
import dev.zanckor.mod.common.util.Mathematic;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.time.chrono.MinguoEra;

import static dev.zanckor.mod.QuestApiMain.MOD_ID;
import static dev.zanckor.mod.common.util.MCUtilClient.properNoun;


public class ItemTargetType extends AbstractTargetType {

    @Override
    public MutableComponent handler(String resourceLocationString, UserGoal goal, Player player, ChatFormatting chatFormatting, ChatFormatting chatFormatting1) {
        Item itemTarget = ForgeRegistries.ITEMS.getValue(new ResourceLocation(resourceLocationString));
        String translationKey = itemTarget.getDescriptionId(itemTarget.getDefaultInstance());

        return MCUtilClient.formatString(properNoun(I18n.get(translationKey)), " " + goal.getCurrentAmount() + "/" + goal.getAmount(),
                chatFormatting, chatFormatting1);
    }

    @Override
    public String target(String resourceLocationString) {
        Item itemTarget = ForgeRegistries.ITEMS.getValue(new ResourceLocation(resourceLocationString));
        String translationKey = itemTarget.getDescriptionId(itemTarget.getDefaultInstance());

        return properNoun(I18n.get(translationKey));
    }

    @Override
    public void renderTarget(PoseStack poseStack, int xPosition, int yPosition, double size, double rotation, UserGoal goal, String resourceLocationString) {
        ItemStack itemStack = ForgeRegistries.ITEMS.getValue(new ResourceLocation(resourceLocationString)).getDefaultInstance();
        rotation = goal.getCurrentAmount() >= goal.getAmount() ? rotation : 0;

        poseStack.pushPose();
        MCUtilClient.renderItem(itemStack, xPosition, yPosition, size, rotation, poseStack);
        poseStack.popPose();
    }
}