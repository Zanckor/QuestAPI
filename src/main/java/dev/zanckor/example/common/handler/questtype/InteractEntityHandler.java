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

public class InteractEntityHandler extends AbstractQuest {

    public void handler(Player player, Entity entity, Gson gson, File file, UserQuest userQuest) throws IOException {

        for (int targetIndex = 0; targetIndex < userQuest.getQuest_target().size(); targetIndex++) {
            userQuest = (UserQuest) GsonManager.getJson(file, UserQuest.class);

            //Checks if interacted entity equals to target and if it is, checks if current progress is more than target amount
            if (!(userQuest.getQuest_target().get(targetIndex).equals(entity.getType().getDescriptionId())) || userQuest.getTarget_current_quantity().get(targetIndex) >= userQuest.getTarget_quantity().get(targetIndex))
                continue;

            FileWriter interactWriter = new FileWriter(file);
            gson.toJson(userQuest.incrementProgress(userQuest, targetIndex), interactWriter);
            interactWriter.flush();
            interactWriter.close();
        }

        userQuest = (UserQuest) GsonManager.getJson(file, UserQuest.class);

        SendQuestPacket.TO_CLIENT(player, new QuestTracked(userQuest));
        CompleteQuest.completeQuest(player, gson, file);
    }
}