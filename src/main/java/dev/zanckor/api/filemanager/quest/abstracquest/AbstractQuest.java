package dev.zanckor.api.filemanager.quest.abstracquest;

import com.google.gson.Gson;
import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.quest.ServerQuest;
import dev.zanckor.api.filemanager.quest.UserQuest;
import dev.zanckor.api.filemanager.quest.register.QuestTemplateRegistry;
import dev.zanckor.example.ModExample;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestReward;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestType;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.quest.ToastPacket;
import dev.zanckor.mod.common.network.message.screen.SetQuestTracked;
import dev.zanckor.mod.common.util.GsonManager;
import dev.zanckor.mod.common.util.MCUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static dev.zanckor.mod.QuestApiMain.*;

public abstract class AbstractQuest {

    /**
     * Abstract class to call a registered quest type handler
     *
     * @param player      The player
     * @param entity
     * @param gson        Gson used to write/read files
     * @param file        File used to write/read player's quest data
     * @param playerQuest ClientQuestBase class that contains player's quest data
     * @param indexGoal
     * @param questType
     * @throws IOException Exception fired when server cannot read json file
     * @see EnumQuestType Types of quests
     * @see ModExample Main class where you should register quest's types
     */

    public abstract void handler(Player player, Entity entity, Gson gson, File file, UserQuest playerQuest, int indexGoal, Enum questType) throws IOException;

    public void completeQuest(Player player, File file, UserQuest.QuestGoal questGoal, int indexGoal, Enum questType) throws IOException {
        Path userFolder = Paths.get(playerData.toFile().toString(), player.getUUID().toString());
        UserQuest userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
        int indexGoals = 0;

        if (userQuest == null) return;

        //Update data
        AbstractQuest quest = QuestTemplateRegistry.getQuestTemplate(questType);
        quest.updateData(player, file);
        userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);

        //Checks each target
        for (UserQuest.QuestGoal goal : userQuest.getQuestGoals()) {
            indexGoals++;

            //Checks if current goal is completed
            if (goal.getCurrentAmount() < goal.getAmount()) {
                return;
            }

            //Only executes code if it's on last index/goal, so is only executed on complete quest
            if (indexGoals < userQuest.getQuestGoals().size()) continue;

            //Changes quest to completed
            FileWriter writer = new FileWriter(file);
            userQuest.setCompleted(true);
            GsonManager.gson().toJson(userQuest, writer);
            writer.close();

            //Changes tracked quest to next available
            for (File activeQuestFile : getActiveQuest(userFolder).toFile().listFiles()) {
                if (!(activeQuestFile.exists())) continue;
                UserQuest playerQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);

                if (playerQuest == null) continue;
                SendQuestPacket.TO_CLIENT(player, new SetQuestTracked(playerQuest));
            }

            //Gives rewards and send a notification to player
            if (userQuest.isCompleted() && MCUtil.isQuestCompleted(userQuest)) {

                //Checks each goal and executes custom complete code
                for (UserQuest.QuestGoal goals : userQuest.getQuestGoals()) {
                    for (AbstractQuest goalEnhanced : QuestTemplateRegistry.getAllQuestTemplates().values()) {
                        goalEnhanced.enhancedCompleteQuest(player, file, goals, indexGoal, questType);
                    }
                }

                giveReward(player, file, userQuest, userFolder);
                SendQuestPacket.TO_CLIENT(player, new ToastPacket(userQuest.getTitle()));
            }
        }
    }

    public abstract void enhancedCompleteQuest(Player player, File file, UserQuest.QuestGoal goals, int indexGoal, Enum questType) throws IOException;

    public void giveReward(Player player, File file, UserQuest userQuest, Path userFolder) throws IOException {
        if (!(userQuest.isCompleted())) return;
        String questName = userQuest.getId() + ".json";

        for (File serverFile : serverQuests.toFile().listFiles()) {
            if (!(serverFile.getName().equals(questName))) continue;

            FileReader serverQuestReader = new FileReader(serverFile);
            ServerQuest serverQuest = GsonManager.gson().fromJson(serverQuestReader, ServerQuest.class);
            serverQuestReader.close();

            //Gives each reward to payer
            for (int rewardIndex = 0; rewardIndex < serverQuest.getRewards().size(); rewardIndex++) {
                AbstractReward reward = QuestTemplateRegistry.getQuestReward(EnumQuestReward.valueOf(serverQuest.getRewards().get(rewardIndex).getType()));
                reward.handler(player, serverQuest, rewardIndex);
            }

            Files.deleteIfExists(Paths.get(getCompletedQuest(userFolder).toString(), file.getName()));
            Files.move(file.toPath(), Paths.get(getCompletedQuest(userFolder).toString(), file.getName()));

            for (int indexGoals = 0; indexGoals < userQuest.getQuestGoals().size(); indexGoals++) {
                LocateHash.movePathQuest(userQuest.getId(), Paths.get(getCompletedQuest(userFolder).toString(), questName), EnumQuestType.valueOf(userQuest.getQuestGoals().get(indexGoals).getType()));
            }
        }
    }

    public abstract void updateData(Player player, File file) throws IOException;
}
