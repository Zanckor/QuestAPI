package dev.zanckor.example.common.handler.questtype;

import com.google.gson.Gson;
import dev.zanckor.api.filemanager.quest.UserQuest;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractQuest;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.screen.UpdateQuestTracked;
import dev.zanckor.mod.common.util.GsonManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class KillHandler extends AbstractQuest {

    public void handler(Player player, Entity entity, Gson gson, File file, UserQuest userQuest, int indexGoal, Enum questType) throws IOException {
        userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
        UserQuest.QuestGoal questGoal = userQuest.getQuestGoals().get(indexGoal);

        //Checks if killed entity equals to target and if it is, checks if current progress is more than target amount
        if (questGoal.getCurrentAmount() >= questGoal.getAmount() || !(questGoal.getTarget().equals(EntityType.getKey(entity.getType()).toString())))
            return;

        questGoal.incrementCurrentAmount(1);

        FileWriter interactWriter = new FileWriter(file);
        gson.toJson(userQuest, interactWriter);
        interactWriter.flush();
        interactWriter.close();

        userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);

        SendQuestPacket.TO_CLIENT(player, new UpdateQuestTracked(userQuest));
        completeQuest(player, file, questGoal, indexGoal, questType);
    }

    @Override
    public void enhancedCompleteQuest(Player player, File file, UserQuest.QuestGoal goals, int indexGoal, Enum questType) throws IOException {

    }

    @Override
    public void updateData(Player player, File file) throws IOException {

    }
}
