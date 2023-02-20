package dev.zanckor.example.common.handler.questtype;

import com.google.gson.Gson;
import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.quest.UserQuest;
import dev.zanckor.api.filemanager.quest.ServerQuest;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractReward;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestReward;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestType;
import dev.zanckor.api.filemanager.quest.register.TemplateRegistry;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.quest.ToastPacket;
import dev.zanckor.mod.common.network.message.screen.QuestTracked;
import dev.zanckor.mod.common.util.GsonManager;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static dev.zanckor.mod.QuestApiMain.*;

public class CompleteQuest {

    public static void completeQuest(Player player, Gson gson, File file) throws IOException {
        Path userFolder = Paths.get(playerData.toFile().toString(), player.getUUID().toString());
        UserQuest modifiedPlayerQuest = (UserQuest) GsonManager.getJson(file, UserQuest.class);


        if (modifiedPlayerQuest.getTarget_current_quantity().equals(modifiedPlayerQuest.getTarget_quantity())) {
            FileWriter completeQuestWriter = new FileWriter(file);
            modifiedPlayerQuest.setCompleted(true);
            gson.toJson(modifiedPlayerQuest, completeQuestWriter);

            completeQuestWriter.close();
            giveReward(player, modifiedPlayerQuest, gson, file, userFolder);

            SendQuestPacket.TO_CLIENT(player, new ToastPacket(modifiedPlayerQuest.getTitle()));
        }

        for (File activeQuestFile : getActiveQuest(userFolder).toFile().listFiles()) {
            if (activeQuestFile.exists()) {
                UserQuest playerQuest = (UserQuest) GsonManager.getJson(file, UserQuest.class);

                if(playerQuest == null) continue;
                SendQuestPacket.TO_CLIENT(player, new QuestTracked(playerQuest));
            }
        }
    }


    public static void giveReward(Player player, UserQuest modifiedPlayerQuest, Gson gson, File file, Path userFolder) throws IOException {
        String questName = modifiedPlayerQuest.getId() + ".json";

        if (modifiedPlayerQuest.isCompleted()) {
            for (File serverFile : serverQuests.toFile().listFiles()) {
                if (serverFile.getName().equals(questName)) {

                    FileReader serverQuestReader = new FileReader(serverFile);
                    ServerQuest serverQuest = gson.fromJson(serverQuestReader, ServerQuest.class);
                    AbstractReward reward = TemplateRegistry.getQuestReward(EnumQuestReward.valueOf(serverQuest.getReward_type()));

                    reward.handler(player, serverQuest);
                    serverQuestReader.close();

                    Files.move(file.toPath(), Paths.get(getCompletedQuest(userFolder).toString(), file.getName()));

                    LocateHash.movePathQuest(modifiedPlayerQuest.getId(), Paths.get(getCompletedQuest(userFolder).toString(), questName), EnumQuestType.valueOf(modifiedPlayerQuest.getQuest_type()));
                }
            }
        }
    }
}
