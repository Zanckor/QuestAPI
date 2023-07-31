package dev.zanckor.example.common.handler.targettype;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractTargetType;
import dev.zanckor.api.filemanager.quest.codec.user.UserGoal;
import dev.zanckor.mod.common.util.MCUtilClient;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

import static dev.zanckor.mod.common.util.MCUtilClient.properNoun;


public class EntityTargetType extends AbstractTargetType {

    @Override
    public MutableComponent handler(String resourceLocationString, UserGoal goal, Player player, ChatFormatting chatFormatting, ChatFormatting chatFormatting1) {
        EntityType<?> translationKey = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(resourceLocationString));

        return MCUtilClient.formatString(properNoun(I18n.get(translationKey.getDescriptionId())), " " + goal.getCurrentAmount() + "/" + goal.getAmount(),
                chatFormatting, chatFormatting1);
    }

    @Override
    public String target(String resourceLocationString) {
        EntityType<?> translationKey = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(resourceLocationString));

        return properNoun(I18n.get(translationKey.getDescriptionId()));
    }


    @Override
    public void renderTarget(PoseStack poseStack, int xPosition, int yPosition, double size, double rotation, UserGoal goal, String resourceLocationString) {
        EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(resourceLocationString));
        rotation = goal.getCurrentAmount() >= goal.getAmount() ? rotation : 0;

        MCUtilClient.renderEntity(
                xPosition + 5, yPosition + 4, 5,
                rotation * 5, (LivingEntity) entityType.create(Minecraft.getInstance().level), poseStack);
    }
}
