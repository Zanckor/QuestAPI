package dev.zanckor.example.common.handler.questtype;

import com.google.gson.Gson;
import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.quest.UserQuest;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractQuest;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestType;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.screen.QuestTracked;
import dev.zanckor.mod.common.util.Timer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static dev.zanckor.mod.QuestApiMain.playerData;

public class ProtectEntityHandler extends AbstractQuest {

    public void handler(Player player, Gson gson, File file, UserQuest playerQuest) throws IOException {
        Path userFolder = Paths.get(playerData.toString(), player.getUUID().toString());
        Path uncompletedQuest = Paths.get(userFolder.toString(), "uncompleted-quests");

        UUID entityUUID = UUID.fromString(playerQuest.getQuest_target().get(0));
        Entity entity = player.getServer().overworld().getEntity(entityUUID);

        FileWriter protectEntityWriter = new FileWriter(file);

        if (Timer.canUseWithCooldown(player.getUUID(), playerQuest.getId(), playerQuest.getTimeLimitInSeconds()) && entity.isAlive()) {
            playerQuest.setTarget_current_quantity(playerQuest.getTarget_quantity().get(0), 0);

            gson.toJson(playerQuest, protectEntityWriter);
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

            LocateHash.movePathQuest(playerQuest.getId(), file.toPath().toAbsolutePath(), EnumQuestType.valueOf(playerQuest.getQuest_type()));
        }

        playerQuest.setTimeLimitInSeconds((int) Timer.remainingTime(player.getUUID(), playerQuest.getId() + ".json"));
        SendQuestPacket.TO_CLIENT(player, new QuestTracked(playerQuest));
    }
}
