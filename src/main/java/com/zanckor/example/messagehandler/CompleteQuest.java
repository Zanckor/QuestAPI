package com.zanckor.example.messagehandler;

import com.google.gson.Gson;
import com.zanckor.mod.PlayerQuest;
import com.zanckor.api.questregister.QuestTemplate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

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

        FileReader completeQuestReader = new FileReader(file);
        PlayerQuest modifiedPlayerQuest = gson.fromJson(completeQuestReader, PlayerQuest.class);
        completeQuestReader.close();

        if (modifiedPlayerQuest.getTarget_current_quantity().equals(modifiedPlayerQuest.getTarget_quantity())) {
            FileWriter completeQuestWriter = new FileWriter(file);
            modifiedPlayerQuest.setCompleted(true);
            gson.toJson(modifiedPlayerQuest, completeQuestWriter);

            completeQuestWriter.close();

            giveReward(player, modifiedPlayerQuest, gson, file, userFolder);
        }
    }




    public static void giveReward(Player player, PlayerQuest modifiedPlayerQuest, Gson gson, File file, Path userFolder) throws IOException {
        if (modifiedPlayerQuest.isCompleted()) {
            for (File serverFile : serverQuests.toFile().listFiles()) {
                if (serverFile.getName().equals("id_" + modifiedPlayerQuest.getId() + ".json")) {

                    FileReader serverQuestReader = new FileReader(serverFile);
                    QuestTemplate serverQuest = gson.fromJson(serverQuestReader, QuestTemplate.class);

                    switch (serverQuest.getReward_type()) {
                        case "ITEM" -> {
                            for (int rewardIndex = 0; rewardIndex < serverQuest.getReward().size(); rewardIndex++) {
                                String valueItem = serverQuest.getReward().get(rewardIndex);
                                int quantity = serverQuest.getReward_quantity().get(rewardIndex);

                                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(valueItem));
                                ItemStack stack = new ItemStack(item, quantity);

                                player.addItem(stack);
                            }

                            break;
                        }

                        case "COMMAND" -> {
                            for (int rewardIndex = 0; rewardIndex < serverQuest.getReward().size(); rewardIndex++) {


                            }


                            break;
                        }
                    }

                    Files.move(file.toPath(), Paths.get(getCompletedQuest(userFolder).toString(), file.getName()));
                    serverQuestReader.close();
                }
            }
        }
    }
}
