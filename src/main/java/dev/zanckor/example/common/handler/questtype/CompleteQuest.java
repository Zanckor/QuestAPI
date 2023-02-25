package dev.zanckor.example.common.handler.questtype;

import com.google.gson.Gson;
import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.quest.ServerQuest;
import dev.zanckor.api.filemanager.quest.UserQuest;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractReward;
import dev.zanckor.api.filemanager.quest.register.TemplateRegistry;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestReward;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestType;
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
        UserQuest userQuest = (UserQuest) GsonManager.getJson(file, UserQuest.class);

        if (!(userQuest.getTarget_current_quantity().equals(userQuest.getTarget_quantity()))) return;

        //Changes quest to completed
        FileWriter writer = new FileWriter(file);
        userQuest.setCompleted(true);
        gson.toJson(userQuest, writer);
        writer.close();

        //Changes tracked quest to next available
        for (File activeQuestFile : getActiveQuest(userFolder).toFile().listFiles()) {
            if (!(activeQuestFile.exists())) return;
            UserQuest playerQuest = (UserQuest) GsonManager.getJson(file, UserQuest.class);

            if (playerQuest == null) continue;
            SendQuestPacket.TO_CLIENT(player, new QuestTracked(playerQuest));
        }

        giveReward(player, userQuest, gson, file, userFolder);
        SendQuestPacket.TO_CLIENT(player, new ToastPacket(userQuest.getTitle()));
    }


    public static void giveReward(Player player, UserQuest userQuest, Gson gson, File file, Path userFolder) throws IOException {
        if (!(userQuest.isCompleted())) return;
        String questName = userQuest.getId() + ".json";

        for (File serverFile : serverQuests.toFile().listFiles()) {
            if (!(serverFile.getName().equals(questName))) return;

            FileReader serverQuestReader = new FileReader(serverFile);
            ServerQuest serverQuest = gson.fromJson(serverQuestReader, ServerQuest.class);
            serverQuestReader.close();

            //Gives each reward to payer
            for (int rewardIndex = 0; rewardIndex < serverQuest.getRewards().size(); rewardIndex++) {
                AbstractReward reward = TemplateRegistry.getQuestReward(EnumQuestReward.valueOf(serverQuest.getRewards().get(rewardIndex).getType()));
                reward.handler(player, serverQuest, rewardIndex);
            }

            Files.move(file.toPath(), Paths.get(getCompletedQuest(userFolder).toString(), file.getName()));
            LocateHash.movePathQuest(userQuest.getId(), Paths.get(getCompletedQuest(userFolder).toString(), questName), EnumQuestType.valueOf(userQuest.getQuest_type()));
        }
    }
}
