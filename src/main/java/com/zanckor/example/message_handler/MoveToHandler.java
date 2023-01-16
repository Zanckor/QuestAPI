package com.zanckor.example.message_handler;

import com.google.gson.Gson;
import com.zanckor.api.quest_register.AbstractQuest;
import com.zanckor.questapi.createQuest.PlayerQuest;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MoveToHandler extends AbstractQuest {

    public void handler(Player player, Gson gson, File file) throws IOException {

        FileReader reader = new FileReader(file);
        PlayerQuest playerQuest = gson.fromJson(reader, PlayerQuest.class);
        reader.close();

        for (int i = 0; i < 3; i++) {
            FileWriter reachCoordWriter = new FileWriter(file);
            gson.toJson(playerQuest.setProgress(playerQuest, i, 1), reachCoordWriter);
            reachCoordWriter.flush();
            reachCoordWriter.close();
        }

        CompleteQuest.completeQuest(player, gson, file);
    }
}
