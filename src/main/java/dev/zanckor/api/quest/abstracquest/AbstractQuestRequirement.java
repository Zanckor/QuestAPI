package dev.zanckor.api.quest.abstracquest;

import dev.zanckor.api.quest.ServerQuestBase;
import dev.zanckor.example.ModExample;
import dev.zanckor.example.enumregistry.enumquest.EnumQuestRequirement;
import net.minecraft.world.entity.player.Player;

import java.io.IOException;

public abstract class AbstractQuestRequirement {

    /**
     * Abstract class to call a registered quest type handler
     * @see EnumQuestRequirement Types of requirements to access a quest
     * @see ModExample Main class where you should register requirement's types
     * @param player            The player
     * @param questTemplate     ServerQuestBase with global quest data
     * @throws IOException       Exception fired when server cannot read json file
     */

    public abstract boolean handler(Player player, ServerQuestBase questTemplate) throws IOException;
}
