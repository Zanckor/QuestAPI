package dev.zanckor.example.common.handler.questtype;

import com.google.gson.Gson;
import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractQuest;
import dev.zanckor.api.filemanager.quest.codec.UserQuest;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.screen.UpdateQuestTracked;
import dev.zanckor.mod.common.util.GsonManager;
import dev.zanckor.mod.common.util.MCUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static dev.zanckor.example.common.enumregistry.enumquest.EnumQuestType.COLLECT;

public class CollectGoal extends AbstractQuest {

    public void handler(ServerPlayer player, Entity entity, Gson gson, File file, UserQuest userQuest, int indexGoal, Enum questType) throws IOException {
        String questID = userQuest.getId();
        userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
        if (userQuest == null || (!(questID.equals(userQuest.getId())))) return;

        UserQuest.QuestGoal questGoal = userQuest.getQuestGoals().get(indexGoal);

        updateData(player, file);
        SendQuestPacket.TO_CLIENT(player, new UpdateQuestTracked(userQuest));

        completeQuest(player, file, questGoal, indexGoal, questType);
    }

    @Override
    public void enhancedCompleteQuest(ServerPlayer player, File file, UserQuest.QuestGoal goalEnhanced, int indexGoal, Enum questType, AbstractQuest questEnhanced) throws IOException {
        UserQuest userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
        if (userQuest == null) return;

        //Gives rewards and send a notification to player
        if (MCUtil.isQuestCompleted(userQuest)) {
            removeItems(player, LocateHash.getQuestByID(userQuest.getId()), goalEnhanced);
            updateData(player, file);
        }
    }

    @Override
    public void giveReward(ServerPlayer player, File file, UserQuest userQuest, Path userFolder) throws IOException {
        super.giveReward(player, file, userQuest, userFolder);
    }

    public static void removeItems(ServerPlayer player, Path questByID, UserQuest.QuestGoal goalEnhanced) throws IOException {
        if (!(goalEnhanced.getType().contains(COLLECT.name()))) return;

        String valueItem = goalEnhanced.getTarget();
        Item itemTarget = ForgeRegistries.ITEMS.getValue(new ResourceLocation(valueItem));

        int itemSlot = player.getInventory().findSlotMatchingItem(itemTarget.getDefaultInstance());

        if (itemSlot < 0) return;
        player.getInventory().removeItem(itemSlot, goalEnhanced.getAmount());
    }

    public void updateData(ServerPlayer player, File file) throws IOException {
        UserQuest userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
        if (userQuest == null) return;

        for (UserQuest.QuestGoal questGoal : userQuest.getQuestGoals()) {
            if (!(questGoal.getType().equals(COLLECT.name()))) continue;

            int itemCount;
            String valueItem = questGoal.getTarget();
            Item itemTarget = ForgeRegistries.ITEMS.getValue(new ResourceLocation(valueItem));

            FileWriter writer = new FileWriter(file);

            //Checks inventory's items
            if (!player.getInventory().contains(itemTarget.getDefaultInstance())) {
                questGoal.setCurrentAmount(0);
            } else {
                int itemSlot = player.getInventory().findSlotMatchingItem(itemTarget.getDefaultInstance());
                ItemStack item = player.getInventory().getItem(itemSlot);
                itemCount = item.getCount() > questGoal.getAmount() ? questGoal.getAmount() : item.getCount();

                questGoal.setCurrentAmount(itemCount);
            }

            GsonManager.gson().toJson(userQuest, writer);

            writer.flush();
            writer.close();
        }
    }
}
