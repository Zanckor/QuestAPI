package com.zanckor.example.handler.questtype;

import com.google.gson.Gson;
import com.zanckor.api.quest.ClientQuestBase;
import com.zanckor.api.quest.abstracquest.AbstractQuest;
import com.zanckor.mod.network.SendQuestPacket;
import com.zanckor.mod.network.message.screen.QuestTracked;
import com.zanckor.mod.util.MCUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RecollectHandler extends AbstractQuest {

    public void handler(Player player, Gson gson, File file, ClientQuestBase playerQuest) throws IOException {

        int itemCount;

        for (int targetIndex = 0; targetIndex < playerQuest.getQuest_target().size(); targetIndex++) {
            ClientQuestBase recollectPlayerQuest = MCUtil.getJsonClientQuest(file);


            String valueItem = recollectPlayerQuest.getQuest_target().get(targetIndex);
            Item itemTarget = ForgeRegistries.ITEMS.getValue(new ResourceLocation(valueItem));

            if (!player.getInventory().contains(itemTarget.getDefaultInstance())) {
                FileWriter writer = new FileWriter(file);
                gson.toJson(recollectPlayerQuest.setProgress(recollectPlayerQuest, targetIndex, 0), writer);
                writer.flush();
                writer.close();

            } else {
                int itemSlot = player.getInventory().findSlotMatchingItem(itemTarget.getDefaultInstance());
                ItemStack item = player.getInventory().getItem(itemSlot);

                itemCount = item.getCount() > recollectPlayerQuest.getTarget_quantity().get(targetIndex) ? recollectPlayerQuest.getTarget_quantity().get(targetIndex) : item.getCount();

                FileWriter recollectWriter = new FileWriter(file);
                gson.toJson(recollectPlayerQuest.setProgress(recollectPlayerQuest, targetIndex, itemCount), recollectWriter);
                recollectWriter.flush();
                recollectWriter.close();

            }
        }

        ClientQuestBase modifiedPlayerQuest = MCUtil.getJsonClientQuest(file);

        if (modifiedPlayerQuest.getTarget_current_quantity().equals(modifiedPlayerQuest.getTarget_quantity())) {
            for (int targetIndex = 0; targetIndex < playerQuest.getQuest_target().size(); targetIndex++) {
                String valueItem = modifiedPlayerQuest.getQuest_target().get(targetIndex);
                Item itemTarget = ForgeRegistries.ITEMS.getValue(new ResourceLocation(valueItem));

                if (player.getInventory().contains(itemTarget.getDefaultInstance())) {
                    int itemSlot = player.getInventory().findSlotMatchingItem(itemTarget.getDefaultInstance());

                    player.getInventory().removeItem(itemSlot, modifiedPlayerQuest.getTarget_quantity().get(targetIndex));
                }
            }
        }

        SendQuestPacket.TO_CLIENT(player, new QuestTracked(modifiedPlayerQuest));
        CompleteQuest.completeQuest(player, gson, file);
    }
}
