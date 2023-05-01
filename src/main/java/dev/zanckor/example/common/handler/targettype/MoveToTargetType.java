package dev.zanckor.example.common.handler.targettype;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractTargetType;
import dev.zanckor.api.filemanager.quest.codec.user.UserGoal;
import dev.zanckor.mod.common.util.MCUtilClient;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

public class MoveToTargetType extends AbstractTargetType {

    @Override
    public MutableComponent handler(String resourceLocationString, UserGoal goal, Player player, ChatFormatting chatFormatting, ChatFormatting chatFormatting1) {
        return MCUtilClient.formatString("Location", " " + goal.getAdditionalListData().toString().substring(1, goal.getAdditionalListData().toString().length() - 1)
                        + " / " + (int) player.getX() + ", " + (int) player.getY() + ", " + (int) player.getZ(),
                chatFormatting, chatFormatting1);
    }

    @Override
    public String target(String resourceLocationString) {
        return "Location";
    }

    @Override
    public void renderTarget(PoseStack poseStack, int xPosition, int yPosition, double size, double rotation, UserGoal goal, String resourceLocationString) {

    }
}
