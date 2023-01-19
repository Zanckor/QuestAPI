package com.zanckor.example.handler.quest;

import com.google.gson.Gson;
import com.zanckor.api.questregister.abstrac.AbstractQuest;
import com.zanckor.api.questregister.abstrac.PlayerQuest;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MoveToHandler extends AbstractQuest {

    public void handler(Player player, Gson gson, File file, PlayerQuest playerQuest) throws IOException {

        for (int i = 0; i < 3; i++) {
            FileReader moveToReader = new FileReader(file);
            PlayerQuest moveToPlayerQuest = gson.fromJson(moveToReader, PlayerQuest.class);
            moveToReader.close();

            FileWriter moveToCoordWriter = new FileWriter(file);
            gson.toJson(playerQuest.setProgress(moveToPlayerQuest, i, 1), moveToCoordWriter);
            moveToCoordWriter.flush();
            moveToCoordWriter.close();
        }

        CompleteQuest.completeQuest(player, gson, file);
    }
}