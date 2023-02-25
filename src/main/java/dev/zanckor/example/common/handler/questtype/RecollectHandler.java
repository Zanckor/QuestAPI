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

    public void handler(Player player, Entity entity, Gson gson, File file, UserQuest userQuest) throws IOException {
        int itemCount;

        for (int targetIndex = 0; targetIndex < userQuest.getQuest_target().size(); targetIndex++) {
            userQuest = (UserQuest) GsonManager.getJson(file, UserQuest.class);
            String valueItem = userQuest.getQuest_target().get(targetIndex);
            Item itemTarget = ForgeRegistries.ITEMS.getValue(new ResourceLocation(valueItem));
            FileWriter writer = new FileWriter(file);

            if (!player.getInventory().contains(itemTarget.getDefaultInstance())) {
                gson.toJson(userQuest.setProgress(userQuest, targetIndex, 0), writer);
            } else {
                int itemSlot = player.getInventory().findSlotMatchingItem(itemTarget.getDefaultInstance());
                ItemStack item = player.getInventory().getItem(itemSlot);
                itemCount = item.getCount() > userQuest.getTarget_quantity().get(targetIndex) ? userQuest.getTarget_quantity().get(targetIndex) : item.getCount();

                gson.toJson(userQuest.setProgress(userQuest, targetIndex, itemCount), writer);
            }

            writer.flush();
            writer.close();
        }

        userQuest = (UserQuest) GsonManager.getJson(file, UserQuest.class);

        if (!(userQuest.getTarget_current_quantity().equals(userQuest.getTarget_quantity()))) return;

        for (int targetIndex = 0; targetIndex < userQuest.getQuest_target().size(); targetIndex++) {
            String valueItem = userQuest.getQuest_target().get(targetIndex);
            Item itemTarget = ForgeRegistries.ITEMS.getValue(new ResourceLocation(valueItem));

            if (player.getInventory().contains(itemTarget.getDefaultInstance())) {
                int itemSlot = player.getInventory().findSlotMatchingItem(itemTarget.getDefaultInstance());
                player.getInventory().removeItem(itemSlot, userQuest.getTarget_quantity().get(targetIndex));
            }
        }

        SendQuestPacket.TO_CLIENT(player, new QuestTracked(userQuest));
        CompleteQuest.completeQuest(player, gson, file);
    }
}
