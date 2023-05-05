package dev.zanckor.api.filemanager.quest.abstracquest;

import com.google.gson.Gson;
import dev.zanckor.api.filemanager.quest.codec.server.ServerQuest;
import dev.zanckor.api.filemanager.quest.codec.user.UserGoal;
import dev.zanckor.api.filemanager.quest.codec.user.UserQuest;
import dev.zanckor.api.filemanager.quest.register.QuestTemplateRegistry;
import dev.zanckor.example.ModExample;
import dev.zanckor.example.common.enumregistry.EnumRegistry;
import dev.zanckor.example.common.enumregistry.enumquest.EnumGoalType;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.quest.ActiveQuestList;
import dev.zanckor.mod.common.network.message.quest.ToastPacket;
import dev.zanckor.mod.common.network.message.screen.UpdateQuestTracked;
import dev.zanckor.mod.common.util.GsonManager;
import dev.zanckor.mod.common.util.MCUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.io.File;
import java.io.IOException;

import static dev.zanckor.mod.QuestApiMain.serverQuests;


public abstract class AbstractGoal {

    /**
     * Abstract class to call a registered quest type handler
     *
     * @param player    The player
     * @param entity
     * @param gson      Gson used to write/read files
     * @param file      File used to write/read player's quest data
     * @param userQuest ClientQuestBase class that contains player's quest data
     * @param indexGoal
     * @param questType
     * @throws IOException Exception fired when server cannot read json file
     * @see EnumGoalType Types of quests
     * @see ModExample Main class where you should register quest's types
     */

    public void handler(ServerPlayer player, Entity entity, Gson gson, File file, UserQuest userQuest, int indexGoal, Enum questType) throws IOException {
        SendQuestPacket.TO_CLIENT(player, new UpdateQuestTracked(userQuest));

        if (MCUtil.isQuestCompleted(userQuest)) completeQuest(player, file);
    }

    protected void completeQuest(ServerPlayer player, File file) throws IOException {
        UserQuest userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
        if (userQuest == null) return;

        //Update file and load it again
        callUpdate(userQuest, player, file);
        userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);

        //Checks if quest is completed and then rewrite file to quest completed.
        //Also gives rewards and send a notification to player
        if (MCUtil.isQuestCompleted(userQuest)) {
            userQuest.setCompleted(true);
            GsonManager.writeJson(file, userQuest);

            callEnhancedReward(userQuest, player, file);

            giveReward(player, file, userQuest);
            SendQuestPacket.TO_CLIENT(player, new ToastPacket(userQuest.getTitle()));
        }

        //Update list of active quests on client side
        SendQuestPacket.TO_CLIENT(player, new ActiveQuestList(player.getUUID()));
    }

    protected abstract void enhancedCompleteQuest(ServerPlayer player, File file, UserGoal userGoal) throws IOException;


    /**
     * Checks each file until get a file corresponding with questID, read it and gives rewards
     */
    protected void giveReward(ServerPlayer player, File file, UserQuest userQuest) throws IOException {
        if (!(userQuest.isCompleted())) return;
        String questID = userQuest.getId() + ".json";

        for (File serverFile : serverQuests.toFile().listFiles()) {
            if (!(serverFile.getName().equals(questID))) continue;
            ServerQuest serverQuest = (ServerQuest) GsonManager.getJsonClass(serverFile, ServerQuest.class);

            if(serverQuest.getRewards() == null) return;

            for (int rewardIndex = 0; rewardIndex < serverQuest.getRewards().size(); rewardIndex++) {
                Enum rewardEnum = EnumRegistry.getEnum(serverQuest.getRewards().get(rewardIndex).getType(), EnumRegistry.getQuestReward());
                AbstractReward reward = QuestTemplateRegistry.getQuestReward(rewardEnum);

                reward.handler(player, serverQuest, rewardIndex);
            }

            MCUtil.moveFileToCompletedFolder(userQuest, player, file);

            return;
        }
    }

    /**
     * Each goal updates his data and check if quest is completed ot run complete method
     */
    protected abstract void updateData(ServerPlayer player, File file) throws IOException;


    /**
     * This method returns what type of goal is your current goal class designed for. See CollectGoal.jar for an example.
     */
    protected abstract Enum getGoalType();


    protected void callUpdate(UserQuest userQuest, ServerPlayer player, File file) throws IOException {
        for (UserGoal goal : userQuest.getQuestGoals()) {
            for (AbstractGoal abstractGoal : QuestTemplateRegistry.getAllGoals().values()) {
                if (goal.getType().equals(abstractGoal.getGoalType().toString())) {
                    abstractGoal.updateData(player, file);
                }
            }
        }
    }

    protected void callEnhancedReward(UserQuest userQuest, ServerPlayer player, File file) throws IOException {
        for (UserGoal goal : userQuest.getQuestGoals()) {
            for (AbstractGoal abstractGoal : QuestTemplateRegistry.getAllGoals().values()) {
                if (goal.getType().equals(abstractGoal.getGoalType().toString())) {
                    abstractGoal.enhancedCompleteQuest(player, file, goal);
                }
            }
        }
    }
}
