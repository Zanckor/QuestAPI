package com.zanckor.example.handler.requirement;

import com.zanckor.api.questregister.abstrac.AbstractRequirement;
import com.zanckor.api.questregister.abstrac.QuestTemplate;
import net.minecraft.world.entity.player.Player;

import java.io.IOException;

public class XpRequirement extends AbstractRequirement {
    @Override
    public boolean handler(Player player, QuestTemplate serverQuest) throws IOException {
        boolean minReqs = player.experienceLevel >= serverQuest.getRequirements_min() ? true : false;
        boolean maxReqs = player.experienceLevel <= serverQuest.getRequirements_max() ? true : false;

        if (!minReqs || !maxReqs) return false;

        return true;
    }
}
