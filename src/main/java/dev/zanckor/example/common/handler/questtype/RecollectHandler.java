package dev.zanckor.example.common.handler.questtype;

import com.google.gson.Gson;
import dev.zanckor.api.filemanager.quest.UserQuest;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractQuest;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.screen.QuestTracked;
import dev.zanckor.mod.common.util.GsonManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RecollectHandler extends AbstractQuest {

    public void handler(Player player, Entity entity, Gson gson, File file, UserQuest playerQuest) throws IOException {
        UserQuest recollectPlayerQuest;
        int itemCount;

        for (int targetIndex = 0; targetIndex < playerQuest.getQuest_target().size(); targetIndex++) {
            recollectPlayerQuest = (UserQuest) GsonManager.getJson(file, UserQuest.class);

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

        recollectPlayerQuest = (UserQuest) GsonManager.getJson(file, UserQuest.class);
        
        if (recollectPlayerQuest.getTarget_current_quantity().equals(recollectPlayerQuest.getTarget_quantity())) {
            for (int targetIndex = 0; targetIndex < playerQuest.getQuest_target().size(); targetIndex++) {
                String valueItem = recollectPlayerQuest.getQuest_target().get(targetIndex);
                Item itemTarget = ForgeRegistries.ITEMS.getValue(new ResourceLocation(valueItem));

                if (player.getInventory().contains(itemTarget.getDefaultInstance())) {
                    int itemSlot = player.getInventory().findSlotMatchingItem(itemTarget.getDefaultInstance());

                    player.getInventory().removeItem(itemSlot, recollectPlayerQuest.getTarget_quantity().get(targetIndex));
                }
            }
        }

        SendQuestPacket.TO_CLIENT(player, new QuestTracked(recollectPlayerQuest));
        CompleteQuest.completeQuest(player, gson, file);
    }
}
