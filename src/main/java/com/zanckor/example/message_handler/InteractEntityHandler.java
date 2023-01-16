package com.zanckor.example.message_handler;

import com.google.gson.Gson;
import com.zanckor.api.quest_register.AbstractQuest;
import com.zanckor.mod.utils.GetLookinAt;
import com.zanckor.questapi.createQuest.PlayerQuest;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class InteractEntityHandler extends AbstractQuest {

    public void handler(Player player, Gson gson, File file) throws IOException {

        FileReader reader = new FileReader(file);
        PlayerQuest playerQuest = gson.fromJson(reader, PlayerQuest.class);
        reader.close();

        for (int targetIndex = 0; targetIndex < playerQuest.getQuest_target().size(); targetIndex++) {
            FileReader interactReader = new FileReader(file);
            PlayerQuest interactPlayerQuest = gson.fromJson(interactReader, PlayerQuest.class);
            interactReader.close();

            Entity entityLookinAt = GetLookinAt.getEntityLookinAt(player, player.getAttributeValue(ForgeMod.ATTACK_RANGE.get()));

            if (interactPlayerQuest.getQuest_target().get(targetIndex).equals(entityLookinAt.getType().getDescriptionId())
                    && interactPlayerQuest.getTarget_current_quantity().get(targetIndex) < interactPlayerQuest.getTarget_quantity().get(targetIndex)) {
                FileWriter interactWriter = new FileWriter(file);
                gson.toJson(interactPlayerQuest.incrementProgress(interactPlayerQuest, targetIndex), interactWriter);
                interactWriter.flush();
                interactWriter.close();
            }
        }

        CompleteQuest.completeQuest(player, gson, file);
    }
}
