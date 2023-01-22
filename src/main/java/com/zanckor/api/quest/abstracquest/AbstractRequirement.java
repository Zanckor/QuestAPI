package com.zanckor.api.quest.abstracquest;

import com.zanckor.api.quest.ServerQuestBase;
import net.minecraft.world.entity.player.Player;

import java.io.IOException;

public abstract class AbstractRequirement {

    public abstract boolean handler(Player player, ServerQuestBase questTemplate) throws IOException;
}
