package com.zanckor.example.handler.quest;

import com.google.gson.Gson;
import com.zanckor.api.database.LocateHash;
import com.zanckor.api.quest.ClientQuestBase;
import com.zanckor.api.quest.abstracquest.AbstractQuest;
import com.zanckor.api.quest.enumquest.EnumQuestType;
import com.zanckor.mod.util.Timer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static com.zanckor.mod.QuestApiMain.playerData;

public class ProtectEntityHandler extends AbstractQuest {

    public void handler(Player player, Gson gson, File file, ClientQuestBase playerQuest) throws IOException {
        Path userFolder = Paths.get(playerData.toString(), player.getUUID().toString());
        Path uncompletedQuest = Paths.get(userFolder.toString(), "uncompleted-quests");

        UUID entityUUID = UUID.fromString(playerQuest.getQuest_target().get(0));
        Entity entity = player.getServer().overworld().getEntity(entityUUID);

        FileWriter protectEntityWriter = new FileWriter(file);

        if (Timer.canUseWithCooldown(player.getUUID(), "id_" + playerQuest.getId(), playerQuest.getTimeLimitInSeconds()) && entity.isAlive()) {
            gson.toJson(playerQuest.incrementProgress(playerQuest, 0), protectEntityWriter);
            entity.remove(Entity.RemovalReason.DISCARDED);

            protectEntityWriter.flush();
            protectEntityWriter.close();

            CompleteQuest.completeQuest(player, gson, file);
        } else if (!entity.isAlive()) {
            playerQuest.setCompleted(true);
            gson.toJson(playerQuest, protectEntityWriter);

            protectEntityWriter.flush();
            protectEntityWriter.close();
            Files.move(file.toPath(), Paths.get(uncompletedQuest.toString(), file.getName()));

            LocateHash.removeQuest(playerQuest.getId(), file.toPath().toAbsolutePath(), EnumQuestType.valueOf(playerQuest.getQuest_type()));
        }
    }
}
