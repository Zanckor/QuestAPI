package dev.zanckor.example.common.handler.questreward;

import dev.zanckor.api.filemanager.quest.abstracquest.AbstractReward;
import dev.zanckor.api.filemanager.quest.codec.server.ServerQuest;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestReward;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.io.IOException;

public class CommandReward extends AbstractReward {

    /**
     * Type of reward, executes command set on quest.json as reward
     *
     * @param player      The player
     * @param serverQuest ServerQuestBase with global quest data
     * @param rewardIndex
     * @throws IOException Exception fired when server cannot read json file
     * @see EnumQuestReward Reward types
     */

    @Override
    public void handler(ServerPlayer player, ServerQuest serverQuest, int rewardIndex) throws IOException {
        String command = serverQuest.getRewards().get(rewardIndex).getTag();
        if(command.contains("@p")) command.replace("@p", player.getScoreboardName());

        int quantity = serverQuest.getRewards().get(rewardIndex).getAmount();

        CommandSourceStack sourceStack = player.getServer().createCommandSourceStack();

        for (int timesExecuted = 0; timesExecuted < quantity; timesExecuted++) {
            sourceStack.getServer().getCommands().performCommand(sourceStack, command);
        }
    }
}