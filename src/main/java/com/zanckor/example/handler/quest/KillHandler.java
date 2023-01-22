package com.zanckor.example.handler.quest;

import com.google.gson.Gson;
import com.zanckor.api.quest.ClientQuestBase;
import com.zanckor.api.quest.abstracquest.AbstractQuest;
import com.zanckor.mod.util.MCUtil;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class KillHandler extends AbstractQuest {

    public void handler(Player player, Gson gson, File file, ClientQuestBase playerQuest) throws IOException {

        for (int targetIndex = 0; targetIndex < playerQuest.getQuest_target().size(); targetIndex++) {
            ClientQuestBase killPlayerQuest = MCUtil.getJsonQuest(file, gson);

            if (killPlayerQuest.getTarget_current_quantity().get(targetIndex) >= killPlayerQuest.getTarget_quantity().get(targetIndex) || !(killPlayerQuest.getQuest_target().get(targetIndex).equals(player.getLastHurtMob().getType().getDescriptionId()))) {
                continue;
            }

            FileWriter killWriter = new FileWriter(file);
            gson.toJson(killPlayerQuest.incrementProgress(killPlayerQuest, targetIndex), killWriter);
            killWriter.flush();
            killWriter.close();
        }

        CompleteQuest.completeQuest(player, gson, file);
    }
}
