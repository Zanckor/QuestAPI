package dev.zanckor.example.common.handler.questtype;

import com.google.gson.Gson;
import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.quest.UserQuest;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractQuest;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestType;
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
import java.nio.file.Path;

public class CollectHandler extends AbstractQuest {

    public void handler(Player player, Entity entity, Gson gson, File file, UserQuest userQuest, int indexGoals) throws IOException {
        int itemCount;
        userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);

        if (userQuest == null) return;
        UserQuest.QuestGoal questGoal = userQuest.getQuestGoals().get(indexGoals);
        String valueItem = questGoal.getTarget();
        Item itemTarget = ForgeRegistries.ITEMS.getValue(new ResourceLocation(valueItem));
        FileWriter writer = new FileWriter(file);

        //Checks inventory's item
        if (!player.getInventory().contains(itemTarget.getDefaultInstance())) {
            questGoal.setCurrentAmount(0);
        } else {
            int itemSlot = player.getInventory().findSlotMatchingItem(itemTarget.getDefaultInstance());
            ItemStack item = player.getInventory().getItem(itemSlot);
            itemCount = item.getCount() > questGoal.getAmount() ? questGoal.getAmount() : item.getCount();

            questGoal.setCurrentAmount(itemCount);
        }
        gson.toJson(userQuest, writer);

        writer.flush();
        writer.close();

        CompleteQuest.completeQuest(player, gson, file);
        Path questPath = LocateHash.getQuestByID(userQuest.getId());
        userQuest = (UserQuest) GsonManager.getJsonClass(questPath.toFile(), UserQuest.class);


        CompleteQuest.completeQuest(player, gson, file);
        SendQuestPacket.TO_CLIENT(player, new QuestTracked(userQuest));
    }
}
