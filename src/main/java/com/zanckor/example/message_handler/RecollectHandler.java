package com.zanckor.example.message_handler;

import com.google.gson.Gson;
import com.zanckor.api.quest_register.AbstractQuest;
import com.zanckor.questapi.createQuest.PlayerQuest;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class RecollectHandler extends AbstractQuest {

    public void handler(Player player, Gson gson, File file) throws IOException {

        FileReader reader = new FileReader(file);
        PlayerQuest playerQuest = gson.fromJson(reader, PlayerQuest.class);
        reader.close();

        int itemCount;

        for (int targetIndex = 0; targetIndex < playerQuest.getQuest_target().size(); targetIndex++) {
            FileReader recollectReader = new FileReader(file);
            PlayerQuest recollectPlayerQuest = gson.fromJson(recollectReader, PlayerQuest.class);
            recollectReader.close();

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


        CompleteQuest.completeQuest(player, gson, file);
    }
}
