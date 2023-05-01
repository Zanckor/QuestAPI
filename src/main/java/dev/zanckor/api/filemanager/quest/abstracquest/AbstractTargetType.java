package dev.zanckor.api.filemanager.quest.abstracquest;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.zanckor.api.filemanager.quest.codec.user.UserGoal;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

public abstract class AbstractTargetType {

    /**
     * Class that returns as a human-readable text the quest target
     *
     * @param resourceLocationString Resource location of the target wanted to translate. Example: entity.minecraft.cow
     * @param player
     * @param chatFormatting
     * @param chatFormatting1
     */

    public abstract MutableComponent handler(String resourceLocationString, UserGoal goal, Player player, ChatFormatting chatFormatting, ChatFormatting chatFormatting1);
    public abstract String target(String resourceLocationString);
    public abstract void renderTarget(PoseStack poseStack, int xPosition, int yPosition, double size, double rotation, UserGoal goal, String resourceLocationString);
}
