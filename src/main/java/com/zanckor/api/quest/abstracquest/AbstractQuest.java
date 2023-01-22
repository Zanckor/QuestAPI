package com.zanckor.api.quest.abstracquest;

import com.google.gson.Gson;
import com.zanckor.api.quest.ClientQuestBase;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.IOException;

public abstract class AbstractQuest {

    public abstract void handler(Player player, Gson gson, File file, ClientQuestBase playerQuest) throws IOException;
}
