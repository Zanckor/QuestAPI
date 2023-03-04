package dev.zanckor.example.common.handler.questtype;

import com.google.gson.Gson;
import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.quest.ServerQuest;
import dev.zanckor.api.filemanager.quest.UserQuest;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractReward;
import dev.zanckor.api.filemanager.quest.register.TemplateRegistry;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestReward;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestType;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.handler.ServerHandler;
import dev.zanckor.mod.common.network.message.quest.ToastPacket;
import dev.zanckor.mod.common.network.message.screen.SetQuestTracked;
import dev.zanckor.mod.common.util.GsonManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static dev.zanckor.mod.QuestApiMain.*;

public class CompleteQuest {

    public static void completeQuest(Player player, Gson gson, File file) throws IOException {
        Path userFolder = Paths.get(playerData.toFile().toString(), player.getUUID().toString());
        UserQuest userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
        if (userQuest == null) return;

        int indexGoals = 0;

        //Checks each target
        for (UserQuest.QuestGoal questGoal : userQuest.getQuestGoals()) {
            indexGoals++;

            if (questGoal.getCurrentAmount() < questGoal.getAmount())
                return;


            //Only executes code if it's on last index/goal
            if (indexGoals < userQuest.getQuestGoals().size()) continue;

            //Changes quest to completed
            FileWriter writer = new FileWriter(file);
            userQuest.setCompleted(true);
            gson.toJson(userQuest, writer);
            writer.close();

            //Changes tracked quest to next available
            for (File activeQuestFile : getActiveQuest(userFolder).toFile().listFiles()) {
                if (!(activeQuestFile.exists())) continue;
                UserQuest playerQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);

                if (playerQuest == null) continue;
                SendQuestPacket.TO_CLIENT(player, new SetQuestTracked(playerQuest));
            }

            giveReward(player, userQuest, file, userFolder);
            SendQuestPacket.TO_CLIENT(player, new ToastPacket(userQuest.getTitle()));

            //Only for COLLECT Type:
            //If quest is completed, items are removed
            CompleteQuest.removeItems(player, LocateHash.getQuestByID(userQuest.getId()));

            //Update collect quests data
            updateInventoryItems(player);
        }
    }

    public static void removeItems(Player player, Path questByID) throws IOException {
        UserQuest userQuest = (UserQuest) GsonManager.getJsonClass(questByID.toFile(), UserQuest.class);

        for (UserQuest.QuestGoal questGoal : userQuest.getQuestGoals()) {
            if (!(questGoal.getType().contains(EnumQuestType.COLLECT.name()))) continue;

            String valueItem = questGoal.getTarget();
            Item itemTarget = ForgeRegistries.ITEMS.getValue(new ResourceLocation(valueItem));

            int itemSlot = player.getInventory().findSlotMatchingItem(itemTarget.getDefaultInstance());
            player.getInventory().removeItem(itemSlot, questGoal.getAmount());
        }
    }

    public static void giveReward(Player player, UserQuest userQuest, File file, Path userFolder) throws IOException {
        if (!(userQuest.isCompleted())) return;
        String questName = userQuest.getId() + ".json";

        for (File serverFile : serverQuests.toFile().listFiles()) {
            if (!(serverFile.getName().equals(questName))) continue;

            FileReader serverQuestReader = new FileReader(serverFile);
            ServerQuest serverQuest = GsonManager.gson().fromJson(serverQuestReader, ServerQuest.class);
            serverQuestReader.close();

            //Gives each reward to payer
            for (int rewardIndex = 0; rewardIndex < serverQuest.getRewards().size(); rewardIndex++) {
                AbstractReward reward = TemplateRegistry.getQuestReward(EnumQuestReward.valueOf(serverQuest.getRewards().get(rewardIndex).getType()));
                reward.handler(player, serverQuest, rewardIndex);
            }

            Files.deleteIfExists(Paths.get(getCompletedQuest(userFolder).toString(), file.getName()));
            Files.move(file.toPath(), Paths.get(getCompletedQuest(userFolder).toString(), file.getName()));

            for (int indexGoals = 0; indexGoals < userQuest.getQuestGoals().size(); indexGoals++) {
                LocateHash.movePathQuest(userQuest.getId(), Paths.get(getCompletedQuest(userFolder).toString(), questName), EnumQuestType.valueOf(userQuest.getQuestGoals().get(indexGoals).getType()));
            }
        }
    }

    public static void updateInventoryItems(Player player) throws IOException {
        ServerHandler.questHandler(EnumQuestType.COLLECT, (ServerPlayer) player, null);
    }


    public static boolean isQuestCompleted(UserQuest userQuest) throws IOException {
        int indexGoals = 0;

        for (UserQuest.QuestGoal questGoal : userQuest.getQuestGoals()) {
            indexGoals++;

            if (questGoal.getCurrentAmount() < questGoal.getAmount()) return false;

            if (indexGoals < userQuest.getQuestGoals().size()) continue;

            return true;
        }

        return false;
    }
}
