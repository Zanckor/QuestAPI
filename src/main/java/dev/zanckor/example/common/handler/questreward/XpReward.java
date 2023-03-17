package dev.zanckor.example.common.handler.questreward;

import dev.zanckor.api.filemanager.quest.abstracquest.AbstractReward;
import dev.zanckor.api.filemanager.quest.codec.ServerQuest;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestReward;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;

public class XpReward extends AbstractReward {

    /**
     * Type of reward, gives player X quantity of xp set on quest.json as reward
     *
     * @param player      The player
     * @param serverQuest ServerQuestBase with global quest data
     * @param rewardIndex
     * @throws IOException Exception fired when server cannot read json file
     * @see EnumQuestReward Reward types
     */

    @Override
    public void handler(ServerPlayer player, ServerQuest serverQuest, int rewardIndex) throws IOException {
        EnumQuestReward type = EnumQuestReward.valueOf(serverQuest.getRewards().get(rewardIndex).getTag());
        int quantity = serverQuest.getRewards().get(rewardIndex).getAmount();

        switch (type){
            case LEVEL -> player.giveExperienceLevels(quantity);
            case POINTS -> player.giveExperiencePoints(quantity);
        }
    }
}