package dev.zanckor.example.common.handler.questreward;

import dev.zanckor.api.filemanager.quest.abstracquest.AbstractReward;
import dev.zanckor.api.filemanager.quest.codec.server.ServerQuest;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestReward;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LootTableReward extends AbstractReward {

    /**
     * Type of reward, gives player whatever item is set on quest.json as reward
     *
     * @param player      The player
     * @param serverQuest ServerQuestBase with global quest data
     * @param rewardIndex
     * @throws IOException Exception fired when server cannot read json file
     * @see EnumQuestReward Reward types
     */

    @Override
    public void handler(ServerPlayer player, ServerQuest serverQuest, int rewardIndex) throws IOException {
        String lootTableRL = serverQuest.getRewards().get(rewardIndex).getTag();
        int rolls = serverQuest.getRewards().get(rewardIndex).getAmount();
        MinecraftServer server = player.server;
        List<ItemStack> itemStackList = new ArrayList<>();

        ResourceLocation rl = new ResourceLocation(lootTableRL);
        LootTable lootTable = server.getLootTables().get(rl);

        for(int actualRoll = 0; actualRoll < rolls; actualRoll++) {
            LootContext.Builder builder = new LootContext.Builder(player.getLevel()).withLuck(player.getLuck());
            itemStackList = lootTable.getRandomItems(builder.create(LootContextParamSets.EMPTY));
        }

        for(ItemStack itemStack : itemStackList){
            player.getInventory().add(itemStack);
        }
    }
}