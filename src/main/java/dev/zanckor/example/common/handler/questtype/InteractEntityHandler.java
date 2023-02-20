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

    public void handler(Player player, Entity entity, Gson gson, File file, UserQuest playerQuest) throws IOException {
        UserQuest interactPlayerQuest;

        for (int targetIndex = 0; targetIndex < playerQuest.getQuest_target().size(); targetIndex++) {
            interactPlayerQuest = (UserQuest) GsonManager.getJson(file, UserQuest.class);

            if (interactPlayerQuest.getQuest_target().get(targetIndex).equals(entity.getType().getDescriptionId())
                    && interactPlayerQuest.getTarget_current_quantity().get(targetIndex) < interactPlayerQuest.getTarget_quantity().get(targetIndex)) {

                FileWriter interactWriter = new FileWriter(file);
                gson.toJson(interactPlayerQuest.incrementProgress(interactPlayerQuest, targetIndex), interactWriter);
                interactWriter.flush();
                interactWriter.close();
            }
        }

        interactPlayerQuest = (UserQuest) GsonManager.getJson(file, UserQuest.class);

        SendQuestPacket.TO_CLIENT(player, new QuestTracked(interactPlayerQuest));
        CompleteQuest.completeQuest(player, gson, file);
    }
}