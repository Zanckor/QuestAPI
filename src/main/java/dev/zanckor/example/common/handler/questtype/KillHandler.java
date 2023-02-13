package dev.zanckor.example.common.handler.questtype;

import com.google.gson.Gson;
import dev.zanckor.api.filemanager.quest.UserQuest;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractQuest;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.screen.QuestTracked;
import dev.zanckor.mod.common.util.GsonManager;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class KillHandler extends AbstractQuest {

    public void handler(Player player, Gson gson, File file, UserQuest playerQuest) throws IOException {
        UserQuest killPlayerQuest;

        for (int targetIndex = 0; targetIndex < playerQuest.getQuest_target().size(); targetIndex++) {
            killPlayerQuest = (UserQuest) GsonManager.getJson(file, UserQuest.class);

            if (killPlayerQuest.getTarget_current_quantity().get(targetIndex) >= killPlayerQuest.getTarget_quantity().get(targetIndex) || !(killPlayerQuest.getQuest_target().get(targetIndex).equals(player.getLastHurtMob().getType().getDescriptionId()))) {
                continue;
            }

            FileWriter killWriter = new FileWriter(file);
            gson.toJson(killPlayerQuest.incrementProgress(killPlayerQuest, targetIndex), killWriter);
            killWriter.flush();
            killWriter.close();
        }

        killPlayerQuest = (UserQuest) GsonManager.getJson(file, UserQuest.class);

        SendQuestPacket.TO_CLIENT(player, new QuestTracked(killPlayerQuest));
        CompleteQuest.completeQuest(player, gson, file);
    }
}
