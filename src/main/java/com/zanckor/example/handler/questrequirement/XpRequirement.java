package com.zanckor.example.handler.questrequirement;

import com.zanckor.api.quest.abstracquest.AbstractRequirement;
import com.zanckor.api.quest.ServerQuestBase;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.io.IOException;

public class XpRequirement extends AbstractRequirement {
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