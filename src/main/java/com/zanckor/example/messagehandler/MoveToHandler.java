package com.zanckor.example.messagehandler;

import com.google.gson.Gson;
import com.zanckor.api.questregister.AbstractQuest;
import com.zanckor.mod.PlayerQuest;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MoveToHandler extends AbstractQuest {

    public void handler(Player player, Gson gson, File file, PlayerQuest playerQuest) throws IOException {

        for (int i = 0; i < 3; i++) {
            FileWriter reachCoordWriter = new FileWriter(file);
            gson.toJson(playerQuest.setProgress(playerQuest, i, 1), reachCoordWriter);
            reachCoordWriter.flush();
            reachCoordWriter.close();
        }

        CompleteQuest.completeQuest(player, gson, file);
    }
}
