package dev.zanckor.api.filemanager.quest.abstracquest;

import dev.zanckor.api.filemanager.quest.codec.ServerQuest;
import dev.zanckor.example.ModExample;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestRequirement;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.io.IOException;

public abstract class AbstractReward {

    /**
     * Abstract class to call a registered quest type handler
     *
     * @param player        The player
     * @param questTemplate ServerQuestBase with global quest data
     * @param rewardIndex
     * @throws IOException
     * @see EnumQuestRequirement Types of rewards gave on complete a quest
     * @see ModExample Main class where you should register reward's types
     */

    public abstract void handler(ServerPlayer player, ServerQuest questTemplate, int rewardIndex) throws IOException;
}
