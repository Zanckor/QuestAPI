package dev.zanckor.example.common.handler.questrequirement;

import dev.zanckor.api.filemanager.quest.abstracquest.AbstractQuestRequirement;
import dev.zanckor.api.filemanager.quest.codec.server.ServerQuest;
import dev.zanckor.api.filemanager.quest.codec.server.ServerRequirement;
import dev.zanckor.example.common.enumregistry.enumdialog.EnumDialogReq;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;

import java.io.IOException;

public class XpRequirement extends AbstractQuestRequirement {

    /**
     * Type of requirement that checks if player is between two values of XP
     *
     * @param player           The player
     * @param serverQuest      ServerQuestBase with global quest data
     * @param requirementIndex
     * @throws IOException Exception fired when server cannot read json file
     * @see EnumDialogReq Requirement types
     */

    @Override
    public boolean handler(Player player, ServerQuest serverQuest, int requirementIndex) throws IOException {
        ServerRequirement requirement = serverQuest.getRequirements().get(requirementIndex);
        if (player.experienceLevel < 0) player.experienceLevel = 0;

        boolean hasReqs = player.experienceLevel >= requirement.getRequirements_min() && player.experienceLevel <= requirement.getRequirements_max();

        if (!hasReqs) {
            player.sendMessage(new TextComponent("Player " + player.getScoreboardName() + " doesn't have the requirements to access to this quest"), player.getUUID());
            player.sendMessage(new TextComponent("Minimum: " + requirement.getRequirements_min()), player.getUUID());
            player.sendMessage(new TextComponent("Maximum: " + requirement.getRequirements_max()), player.getUUID());
        }

        return hasReqs;
    }
}