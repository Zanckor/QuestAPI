package dev.zanckor.example.common.handler.questtype;

import com.google.gson.Gson;
import dev.zanckor.api.filemanager.quest.UserQuest;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractQuest;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.screen.QuestTracked;
import dev.zanckor.mod.common.util.GsonManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static dev.zanckor.example.common.enumregistry.enumquest.EnumQuestType.MOVE_TO;

public class MoveToHandler extends AbstractQuest {

    public void handler(Player player, Entity entity, Gson gson, File file, UserQuest userQuest, int indexGoals) throws IOException {

        UserQuest.QuestGoal questGoal = userQuest.getQuestGoals().get(indexGoals);

        if (!(questGoal.getType().equals(MOVE_TO.toString()))) return;
        questGoal.setCurrentAmount(1);

        FileWriter moveToCoordWriter = new FileWriter(file);
        gson.toJson(userQuest, moveToCoordWriter);
        moveToCoordWriter.flush();
        moveToCoordWriter.close();

        userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);

        SendQuestPacket.TO_CLIENT(player, new QuestTracked(userQuest));
        CompleteQuest.completeQuest(player, gson, file);
    }
}