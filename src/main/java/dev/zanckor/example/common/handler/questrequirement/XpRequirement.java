package dev.zanckor.example.common.handler.questrequirement;

import dev.zanckor.api.filemanager.quest.ServerQuest;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractQuestRequirement;
import dev.zanckor.example.common.enumregistry.enumdialog.EnumRequirementType;
import net.minecraft.network.chat.Component;
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
     * @see EnumRequirementType Requirement types
     */

    @Override
    public boolean handler(Player player, ServerQuest serverQuest, int requirementIndex) throws IOException {
        ServerQuest.Requirement requirement = serverQuest.getRequirements().get(requirementIndex);

        boolean hasReqs = player.experienceLevel >= requirement.getRequirements_min() && player.experienceLevel <= requirement.getRequirements_max();

        if (!hasReqs) {
            player.sendSystemMessage(Component.literal("Player " + player.getScoreboardName() + " doesn't have the requirements to access to this quest"));
            player.sendSystemMessage(Component.literal("Minimum: " + requirement.getRequirements_min()));
            player.sendSystemMessage(Component.literal("Maximum: " + requirement.getRequirements_max()));
        }

        return hasReqs;
    }
}