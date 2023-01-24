package com.zanckor.example.handler.requirement;

import com.zanckor.api.quest.abstracquest.AbstractRequirement;
import com.zanckor.api.quest.ServerQuestBase;
import net.minecraft.world.entity.player.Player;

import java.io.IOException;

public class XpRequirement extends AbstractRequirement {
    @Override
    public boolean handler(Player player, ServerQuestBase serverQuest) throws IOException {
        boolean minReqs = player.experienceLevel >= serverQuest.getRequirements_min();
        boolean maxReqs = player.experienceLevel <= serverQuest.getRequirements_max();

        return (minReqs && maxReqs);
    }
}