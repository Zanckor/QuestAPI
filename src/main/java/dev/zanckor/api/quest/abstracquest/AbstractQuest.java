package dev.zanckor.api.quest.abstracquest;

import com.google.gson.Gson;
import dev.zanckor.api.quest.ClientQuestBase;
import dev.zanckor.example.ModExample;
import dev.zanckor.example.enumregistry.enumquest.EnumQuestType;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.IOException;

public abstract class AbstractQuest {

    /**
     * Abstract class to call a registered quest type handler
     * @see  EnumQuestType Types of quests
     * @see ModExample Main class where you should register quest's types
     * @param player        The player
     * @param gson          Gson used to write/read files
     * @param file          File used to write/read player's quest data
     * @param playerQuest   ClientQuestBase class that contains player's quest data
     * @throws IOException  Exception fired when server cannot read json file
     */

    public abstract void handler(Player player, Gson gson, File file, ClientQuestBase playerQuest) throws IOException;
}
