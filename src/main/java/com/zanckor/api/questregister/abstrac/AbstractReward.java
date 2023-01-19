package com.zanckor.api.questregister.abstrac;

import com.google.gson.Gson;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.IOException;

public abstract class AbstractReward {

    public abstract void handler(Player player, QuestTemplate questTemplate) throws IOException;
}
