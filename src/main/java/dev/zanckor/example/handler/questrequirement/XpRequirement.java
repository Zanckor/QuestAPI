package dev.zanckor.example.handler.questrequirement;

import dev.zanckor.api.quest.abstracquest.AbstractQuestRequirement;
import dev.zanckor.api.quest.ServerQuestBase;
import dev.zanckor.example.enumregistry.enumdialog.EnumRequirementType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.io.IOException;

public class XpRequirement extends AbstractQuestRequirement {

    /**
     * Type of requirement that checks if player is between two values of XP
     * @param player            The player
     * @param serverQuest       ServerQuestBase with global quest data
     * @throws IOException      Exception fired when server cannot read json file
     * @see EnumRequirementType Requirement types
     */

    @Override
    public boolean handler(Player player, ServerQuestBase serverQuest) throws IOException {
        boolean minReqs = player.experienceLevel >= serverQuest.getRequirements_min();
        boolean maxReqs = player.experienceLevel <= serverQuest.getRequirements_max();

        boolean hasReqs = minReqs && maxReqs;


        if(!hasReqs){
            player.sendSystemMessage(Component.literal("Player " + player.getScoreboardName() + " doesn't have the requirements to access to this quest"));
            player.sendSystemMessage(Component.literal("Minimum: " + serverQuest.getRequirements_min()));
            player.sendSystemMessage(Component.literal("Maximum: " + serverQuest.getRequirements_max()));
        }

        return hasReqs;
    }
}