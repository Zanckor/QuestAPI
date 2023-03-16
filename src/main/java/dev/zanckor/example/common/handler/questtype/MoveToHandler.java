package dev.zanckor.example.common.handler.questtype;

import com.google.gson.Gson;
import dev.zanckor.api.filemanager.quest.codec.UserQuest;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractQuest;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.screen.UpdateQuestTracked;
import dev.zanckor.mod.common.util.GsonManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static dev.zanckor.example.common.enumregistry.enumquest.EnumQuestType.MOVE_TO;

public class MoveToHandler extends AbstractQuest {

    public void handler(ServerPlayer player, Entity entity, Gson gson, File file, UserQuest userQuest, int indexGoal, Enum questType) throws IOException {

        UserQuest.QuestGoal questGoal = userQuest.getQuestGoals().get(indexGoal);

        if (!(questGoal.getType().equals(MOVE_TO.toString()))) return;
        questGoal.setCurrentAmount(1);

        FileWriter moveToCoordWriter = new FileWriter(file);
        gson.toJson(userQuest, moveToCoordWriter);
        moveToCoordWriter.flush();
        moveToCoordWriter.close();

        userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);

        SendQuestPacket.TO_CLIENT(player, new UpdateQuestTracked(userQuest));
        completeQuest(player, file, questGoal, indexGoal, questType);
    }

    @Override
    public void enhancedCompleteQuest(ServerPlayer player, File file, UserQuest.QuestGoal goals, int indexGoal, Enum questType, AbstractQuest goalEnhanced) throws IOException {

    }

    @Override
    public void updateData(ServerPlayer player, File file) throws IOException {

    }
}