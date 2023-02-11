package dev.zanckor.example.handler.questtype;

import com.google.gson.Gson;
import dev.zanckor.api.quest.ClientQuestBase;
import dev.zanckor.api.quest.abstracquest.AbstractQuest;
import dev.zanckor.mod.network.SendQuestPacket;
import dev.zanckor.mod.network.message.screen.QuestTracked;
import dev.zanckor.mod.util.MCUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class InteractEntityHandler extends AbstractQuest {

    public void handler(Player player, Gson gson, File file, ClientQuestBase playerQuest) throws IOException {

        for (int targetIndex = 0; targetIndex < playerQuest.getQuest_target().size(); targetIndex++) {
            ClientQuestBase interactPlayerQuest = MCUtil.getJsonClientQuest(file);
            Entity entityLookinAt = MCUtil.getEntityLookinAt(player, player.getAttributeValue(ForgeMod.ATTACK_RANGE.get()));

            if (interactPlayerQuest.getQuest_target().get(targetIndex).equals(entityLookinAt.getType().getDescriptionId())
                    && interactPlayerQuest.getTarget_current_quantity().get(targetIndex) < interactPlayerQuest.getTarget_quantity().get(targetIndex)) {

                FileWriter interactWriter = new FileWriter(file);
                gson.toJson(interactPlayerQuest.incrementProgress(interactPlayerQuest, targetIndex), interactWriter);
                interactWriter.flush();
                interactWriter.close();
            }
        }

        SendQuestPacket.TO_CLIENT(player, new QuestTracked(MCUtil.getJsonClientQuest(file)));
        CompleteQuest.completeQuest(player, gson, file);
    }
}