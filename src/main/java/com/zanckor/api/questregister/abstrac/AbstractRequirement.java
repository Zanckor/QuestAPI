package com.zanckor.api.questregister.abstrac;

import net.minecraft.world.entity.player.Player;

import java.io.IOException;

public abstract class AbstractRequirement {

    public abstract boolean handler(Player player, QuestTemplate questTemplate) throws IOException;
}
