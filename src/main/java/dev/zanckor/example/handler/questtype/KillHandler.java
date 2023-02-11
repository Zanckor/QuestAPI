package dev.zanckor.example.handler.questtype;

import com.google.gson.Gson;
import dev.zanckor.api.quest.ClientQuestBase;
import dev.zanckor.api.quest.abstracquest.AbstractQuest;
import dev.zanckor.mod.network.SendQuestPacket;
import dev.zanckor.mod.network.message.screen.QuestTracked;
import dev.zanckor.mod.util.MCUtil;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class KillHandler extends AbstractQuest {

    public void handler(Player player, Gson gson, File file, ClientQuestBase playerQuest) throws IOException {

        for (int targetIndex = 0; targetIndex < playerQuest.getQuest_target().size(); targetIndex++) {
            ClientQuestBase killPlayerQuest = MCUtil.getJsonClientQuest(file);

            if (killPlayerQuest.getTarget_current_quantity().get(targetIndex) >= killPlayerQuest.getTarget_quantity().get(targetIndex) || !(killPlayerQuest.getQuest_target().get(targetIndex).equals(player.getLastHurtMob().getType().getDescriptionId()))) {
                continue;
            }

            FileWriter killWriter = new FileWriter(file);
            gson.toJson(killPlayerQuest.incrementProgress(killPlayerQuest, targetIndex), killWriter);
            killWriter.flush();
            killWriter.close();
        }

        SendQuestPacket.TO_CLIENT(player, new QuestTracked(MCUtil.getJsonClientQuest(file)));
        CompleteQuest.completeQuest(player, gson, file);
    }
}
