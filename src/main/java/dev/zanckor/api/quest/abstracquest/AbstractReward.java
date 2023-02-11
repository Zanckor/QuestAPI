package dev.zanckor.api.quest.abstracquest;

import dev.zanckor.api.quest.ServerQuestBase;
import dev.zanckor.example.ModExample;
import dev.zanckor.example.enumregistry.enumquest.EnumQuestRequirement;
import net.minecraft.world.entity.player.Player;

import java.io.IOException;

public abstract class AbstractReward {

    /**
     * Abstract class to call a registered quest type handler
     * @see EnumQuestRequirement Types of rewards gave on complete a quest
     * @see ModExample Main class where you should register reward's types
     * @param player        The player
     * @param questTemplate ServerQuestBase with global quest data
     * @throws IOException
     */

    public abstract void handler(Player player, ServerQuestBase questTemplate) throws IOException;
}
