package com.zanckor.example.handler.reward;

import com.zanckor.api.questregister.abstrac.AbstractReward;
import com.zanckor.api.questregister.abstrac.QuestTemplate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;

public class ItemReward extends AbstractReward {
    @Override
    public void handler(Player player, QuestTemplate serverQuest) throws IOException {
        for (int rewardIndex = 0; rewardIndex < serverQuest.getReward().size(); rewardIndex++) {
            String valueItem = serverQuest.getReward().get(rewardIndex);
            int quantity = serverQuest.getReward_quantity().get(rewardIndex);

            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(valueItem));
            ItemStack stack = new ItemStack(item, quantity);

            player.addItem(stack);
        }
    }
}
