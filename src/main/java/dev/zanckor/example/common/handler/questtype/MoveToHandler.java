package dev.zanckor.example.common.handler.questtype;

import com.google.gson.Gson;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractQuest;
import dev.zanckor.api.filemanager.quest.UserQuest;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.screen.QuestTracked;
import dev.zanckor.mod.common.util.GsonManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MoveToHandler extends AbstractQuest {

    public void handler(Player player, Entity entity, Gson gson, File file, UserQuest userQuest) throws IOException {

        for (int i = 0; i < 3; i++) {
            userQuest = (UserQuest) GsonManager.getJson(file, UserQuest.class);

            FileWriter moveToCoordWriter = new FileWriter(file);
            gson.toJson(userQuest.setProgress(userQuest, i, 1), moveToCoordWriter);
            moveToCoordWriter.flush();
            moveToCoordWriter.close();

        }

        userQuest = (UserQuest) GsonManager.getJson(file, UserQuest.class);

        SendQuestPacket.TO_CLIENT(player, new QuestTracked(userQuest));
        CompleteQuest.completeQuest(player, gson, file);
    }
}