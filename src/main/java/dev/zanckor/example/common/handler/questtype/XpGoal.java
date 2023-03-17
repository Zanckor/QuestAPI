package dev.zanckor.example.common.handler.questtype;

import com.google.gson.Gson;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractQuest;
import dev.zanckor.api.filemanager.quest.codec.UserQuest;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.screen.UpdateQuestTracked;
import dev.zanckor.mod.common.util.GsonManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class XpGoal extends AbstractQuest {

    public void handler(ServerPlayer player, Entity entity, Gson gson, File file, UserQuest userQuest, int indexGoal, Enum questType) throws IOException {
        userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
        UserQuest.QuestGoal questGoal = userQuest.getQuestGoals().get(indexGoal);

        if (questGoal.getCurrentAmount() >= questGoal.getAmount()) {
            return;
        }


        int currentAmount = player.experienceLevel >= questGoal.getAmount() ? questGoal.getAmount() : player.experienceLevel;
        questGoal.setCurrentAmount(currentAmount);

        FileWriter interactWriter = new FileWriter(file);
        gson.toJson(userQuest, interactWriter);
        interactWriter.flush();
        interactWriter.close();

        userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);

        SendQuestPacket.TO_CLIENT(player, new UpdateQuestTracked(userQuest));
        completeQuest(player, file, questGoal, indexGoal, questType);
    }

    @Override
    public void enhancedCompleteQuest(ServerPlayer player, File file, UserQuest.QuestGoal goals, int indexGoal, Enum questType, AbstractQuest goalEnhanced) throws IOException {
        player.setExperienceLevels(player.experienceLevel - goals.getAmount());
    }

    @Override
    public void updateData(ServerPlayer player, File file) throws IOException {

    }
}
