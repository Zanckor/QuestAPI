package com.zanckor.example.handler.questtype;

import com.google.gson.Gson;
import com.zanckor.api.database.LocateHash;
import com.zanckor.api.quest.ClientQuestBase;
import com.zanckor.api.quest.ServerQuestBase;
import com.zanckor.api.quest.abstracquest.AbstractReward;
import com.zanckor.api.quest.enumquest.EnumQuestReward;
import com.zanckor.api.quest.enumquest.EnumQuestType;
import com.zanckor.api.quest.register.TemplateRegistry;
import com.zanckor.mod.network.SendQuestPacket;
import com.zanckor.mod.network.message.quest.ToastPacket;
import com.zanckor.mod.util.MCUtil;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.zanckor.mod.QuestApiMain.*;

public class CompleteQuest {

    public static void completeQuest(Player player, Gson gson, File file) throws IOException {
        Path userFolder = Paths.get(playerData.toFile().toString(), player.getUUID().toString());
        ClientQuestBase modifiedPlayerQuest = MCUtil.getJsonClientQuest(file, gson);


        if (modifiedPlayerQuest.getTarget_current_quantity().equals(modifiedPlayerQuest.getTarget_quantity())) {
            FileWriter completeQuestWriter = new FileWriter(file);
            modifiedPlayerQuest.setCompleted(true);
            gson.toJson(modifiedPlayerQuest, completeQuestWriter);

            completeQuestWriter.close();
            giveReward(player, modifiedPlayerQuest, gson, file, userFolder);

            SendQuestPacket.TO_CLIENT(player, new ToastPacket(modifiedPlayerQuest.getTitle()));
        }
    }


    public static void giveReward(Player player, ClientQuestBase modifiedPlayerQuest, Gson gson, File file, Path userFolder) throws IOException {
        if (modifiedPlayerQuest.isCompleted()) {
            for (File serverFile : serverQuests.toFile().listFiles()) {
                if (serverFile.getName().equals("id_" + modifiedPlayerQuest.getId() + ".json")) {

                    FileReader serverQuestReader = new FileReader(serverFile);
                    ServerQuestBase serverQuest = gson.fromJson(serverQuestReader, ServerQuestBase.class);
                    AbstractReward reward = TemplateRegistry.getQuestReward(EnumQuestReward.valueOf(serverQuest.getReward_type()));

                    reward.handler(player, serverQuest);
                    serverQuestReader.close();

                    Files.move(file.toPath(), Paths.get(getCompletedQuest(userFolder).toString(), file.getName()));
                    LocateHash.movePathQuest(modifiedPlayerQuest.getId(), Paths.get(getCompletedQuest(userFolder).toString()), EnumQuestType.valueOf(modifiedPlayerQuest.getQuest_type()));
                }
            }
        }
    }
}
