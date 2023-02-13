package dev.zanckor.example.common.handler.questreward;

import dev.zanckor.api.filemanager.quest.abstracquest.AbstractReward;
import dev.zanckor.api.filemanager.quest.ServerQuest;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestReward;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;

public class ItemReward extends AbstractReward {

    /**
     * Type of reward, gives player whatever item is set on quest.json as reward
     *
     * @param player        The player
     * @param serverQuest   ServerQuestBase with global quest data
     * @throws IOException      Exception fired when server cannot read json file
     * @see EnumQuestReward Reward types
     */

    @Override
    public void handler(Player player, ServerQuest serverQuest) throws IOException {
        for (int rewardIndex = 0; rewardIndex < serverQuest.getReward().size(); rewardIndex++) {
            String valueItem = serverQuest.getReward().get(rewardIndex);
            int quantity = serverQuest.getReward_quantity().get(rewardIndex);

            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(valueItem));
            ItemStack stack = new ItemStack(item, quantity);

            player.addItem(stack);
        }
    }
}