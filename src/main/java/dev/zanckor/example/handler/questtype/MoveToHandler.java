package dev.zanckor.example.handler.questtype;

import com.google.gson.Gson;
import dev.zanckor.api.quest.abstracquest.AbstractQuest;
import dev.zanckor.api.quest.ClientQuestBase;
import dev.zanckor.mod.network.SendQuestPacket;
import dev.zanckor.mod.network.message.screen.QuestTracked;
import dev.zanckor.mod.util.MCUtil;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MoveToHandler extends AbstractQuest {

    public void handler(Player player, Gson gson, File file, ClientQuestBase playerQuest) throws IOException {

        for (int i = 0; i < 3; i++) {
            ClientQuestBase moveToPlayerQuest = MCUtil.getJsonClientQuest(file);

            FileWriter moveToCoordWriter = new FileWriter(file);
            gson.toJson(playerQuest.setProgress(moveToPlayerQuest, i, 1), moveToCoordWriter);
            moveToCoordWriter.flush();
            moveToCoordWriter.close();

        }

        SendQuestPacket.TO_CLIENT(player, new QuestTracked(MCUtil.getJsonClientQuest(file)));
        CompleteQuest.completeQuest(player, gson, file);
    }
}