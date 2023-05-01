package dev.zanckor.example.common.handler.targettype;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractTargetType;
import dev.zanckor.api.filemanager.quest.codec.user.UserGoal;
import dev.zanckor.mod.common.util.MCUtilClient;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import static dev.zanckor.mod.common.util.MCUtilClient.properNoun;

public class DefaultTargetType extends AbstractTargetType {

    @Override
    public MutableComponent handler(String resourceLocationString, UserGoal goal, Player player, ChatFormatting chatFormatting, ChatFormatting chatFormatting1) {
        String translationKey = new ResourceLocation(resourceLocationString).getPath();

        return MCUtilClient.formatString(properNoun(I18n.get(translationKey)), " " + goal.getCurrentAmount() + "/" + goal.getAmount(),
                chatFormatting, chatFormatting1);
    }

    @Override
    public String target(String resourceLocationString) {
        String translationKey = new ResourceLocation(resourceLocationString).getPath();

        return properNoun(I18n.get(translationKey));
    }

    @Override
    public void renderTarget(PoseStack poseStack, int xPosition, int yPosition, double size, double rotation, UserGoal goal, String resourceLocationString) {
        //Renders nothing
    }
}
