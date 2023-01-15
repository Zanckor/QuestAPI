package com.zanckor.questapi.network.messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zanckor.questapi.QuestApi;
import com.zanckor.questapi.createQuest.PlayerQuest;
import com.zanckor.questapi.createQuest.ServerQuest;
import com.zanckor.questapi.utils.GetLookinAt;
import com.zanckor.questapi.utils.QuestTimers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.function.Supplier;

public class QuestData {

    String quest;

    public QuestData(String quest) {
        this.quest = quest;
    }

    public QuestData(FriendlyByteBuf buffer) {
        this.quest = buffer.readUtf();
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
        buffer.writeUtf(quest);
    }


    public static void handle(QuestData msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();

            Path serverDirectory = ctx.get().getSender().server.getServerDirectory().toPath();

            Path questapi = Paths.get(serverDirectory.toString(), "quest-api");
            Path playerdata = Paths.get(questapi.toString(), "player-data");
            Path userFolder = Paths.get(playerdata.toString(), player.getUUID().toString());
            Path activeQuest = Paths.get(userFolder.toString(), "active-quests");


            for (File file : activeQuest.toFile().listFiles()) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();

                try {
                    //Incrementa el proceso de la misión
                    incrementQuestProgress(player, gson, file, msg.quest, serverDirectory, 0, 1);

                    //Completa la misión en caso de cumplir los requisitos
                    completeQuest(player, gson, file, serverDirectory);
                } catch (IOException e) {
                    QuestApi.LOGGER.error(e.getMessage());
                    QuestApi.LOGGER.error(e.getCause().getMessage());
                }
            }
        });

        ctx.get().setPacketHandled(true);

    }


    public static void incrementQuestProgress(Player player, Gson gson, File file, String quest, Path serverDirectory, int position, int times) throws IOException {
        FileReader reader = new FileReader(file);
        PlayerQuest playerQuest = gson.fromJson(reader, PlayerQuest.class);
        reader.close();

        if (playerQuest.completed || !(playerQuest.getQuest_type().equals(quest))) return;

        switch (quest) {
            case "kill" -> {

                for (int targetIndex = 0; targetIndex < playerQuest.getQuest_target().size(); targetIndex++) {
                    FileReader killReader = new FileReader(file);
                    PlayerQuest killPlayerQuest = gson.fromJson(killReader, PlayerQuest.class);
                    killReader.close();

                    if (killPlayerQuest.getTarget_current_quantity().get(targetIndex) >= killPlayerQuest.getTarget_quantity().get(targetIndex) || !(killPlayerQuest.getQuest_target().get(targetIndex).equals(player.getLastHurtMob().getType().getDescriptionId()))) {
                        continue;
                    }

                    FileWriter killWriter = new FileWriter(file);
                    gson.toJson(killPlayerQuest.incrementProgress(killPlayerQuest, targetIndex), killWriter);
                    killWriter.flush();
                    killWriter.close();
                }

                return;
            }

            case "recollect" -> {
                int itemCount;

                if (playerQuest.completed)
                    break;

                for (int targetIndex = 0; targetIndex < playerQuest.getQuest_target().size(); targetIndex++) {
                    FileReader recollectReader = new FileReader(file);
                    PlayerQuest recollectPlayerQuest = gson.fromJson(recollectReader, PlayerQuest.class);
                    recollectReader.close();

                    String valueItem = recollectPlayerQuest.getQuest_target().get(targetIndex);
                    Item itemTarget = ForgeRegistries.ITEMS.getValue(new ResourceLocation(valueItem));

                    if (!player.getInventory().contains(itemTarget.getDefaultInstance())) {
                        FileWriter writer = new FileWriter(file);
                        gson.toJson(recollectPlayerQuest.setProgress(recollectPlayerQuest, targetIndex, 0), writer);
                        writer.flush();
                        writer.close();

                    } else {
                        int itemSlot = player.getInventory().findSlotMatchingItem(itemTarget.getDefaultInstance());
                        ItemStack item = player.getInventory().getItem(itemSlot);

                        itemCount = item.getCount() > recollectPlayerQuest.getTarget_quantity().get(targetIndex) ? recollectPlayerQuest.getTarget_quantity().get(targetIndex) : item.getCount();

                        FileWriter recollectWriter = new FileWriter(file);
                        gson.toJson(recollectPlayerQuest.setProgress(recollectPlayerQuest, targetIndex, itemCount), recollectWriter);
                        recollectWriter.flush();
                        recollectWriter.close();
                    }
                }


                return;
            }

            case "npc_interact" -> {

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

                return;
            }

            case "reach_coord" -> {
                for (int i = 0; i < 3; i++) {
                    FileReader reachCoordRead = new FileReader(file);
                    PlayerQuest reachCoordPlayerQuest = gson.fromJson(reachCoordRead, PlayerQuest.class);
                    reachCoordRead.close();

                    FileWriter reachCoordWriter = new FileWriter(file);
                    gson.toJson(playerQuest.setProgress(reachCoordPlayerQuest, i, 1), reachCoordWriter);
                    reachCoordWriter.flush();
                    reachCoordWriter.close();
                }

                return;
            }

            case "protect_entity" -> {
                if (playerQuest.getQuest_type().equals("protect_entity") && !playerQuest.getQuest_target().contains("entity")) {
                    UUID entityUUID = UUID.fromString(playerQuest.getQuest_target().get(0));

                    Entity entity = player.getServer().overworld().getEntity(entityUUID);

                    FileReader protectEntityRead = new FileReader(file);
                    PlayerQuest protectEntityPlayerQuest = gson.fromJson(protectEntityRead, PlayerQuest.class);
                    protectEntityRead.close();

                    FileWriter protectEntityWriter = new FileWriter(file);

                    if (QuestTimers.canUseWithCooldown(player.getUUID(), "id_" + playerQuest.getId(), playerQuest.getTimeLimitInSeconds()) && entity.isAlive()) {
                        gson.toJson(playerQuest.setProgress(protectEntityPlayerQuest, 0, 1), protectEntityWriter);
                    } else {
                        playerQuest.setCompleted(true);

                        gson.toJson(playerQuest, protectEntityWriter);
                    }

                    protectEntityWriter.flush();
                    protectEntityWriter.close();

                    if (!entity.isAlive()) {
                        Path questapi = Paths.get(serverDirectory.toString(), "quest-api");
                        Path playerdata = Paths.get(questapi.toString(), "player-data");
                        Path userFolder = Paths.get(playerdata.toString(), player.getUUID().toString());
                        Path uncompletedQuest = Paths.get(userFolder.toString(), "uncompleted-quests");

                        Files.move(file.toPath(), Paths.get(uncompletedQuest.toString(), file.getName()));

                    }

                    return;
                }
            }

            default -> {
                FileReader customReader = new FileReader(file);
                PlayerQuest customPlayerQuest = gson.fromJson(customReader, PlayerQuest.class);
                customReader.close();

                if (customPlayerQuest.getTarget_current_quantity().get(position) >= customPlayerQuest.getTarget_quantity().get(position) || !(customPlayerQuest.getQuest_target().get(position).equals(player.getLastHurtMob().getType().getDescriptionId()))) {
                    return;
                }

                FileWriter customWriter = new FileWriter(file);
                gson.toJson(customPlayerQuest.incrementProgress(customPlayerQuest, position), customWriter);
                customWriter.flush();
                customWriter.close();
            }
        }
    }


    public static void completeQuest(Player player, Gson gson, File file,Path serverDirectory) throws IOException {
        Path questapi = Paths.get(serverDirectory.toString(), "quest-api");
        Path playerdata = Paths.get(questapi.toString(), "player-data");
        Path userFolder = Paths.get(playerdata.toString(), player.getUUID().toString());

        FileReader completeQuestReader = new FileReader(file);
        PlayerQuest modifiedPlayerQuest = gson.fromJson(completeQuestReader, PlayerQuest.class);
        completeQuestReader.close();

        if (modifiedPlayerQuest.getTarget_current_quantity().equals(modifiedPlayerQuest.getTarget_quantity())) {
            FileWriter completeQuestWriter = new FileWriter(file);
            modifiedPlayerQuest.setCompleted(true);
            gson.toJson(modifiedPlayerQuest, completeQuestWriter);

            completeQuestWriter.close();


            giveReward(player, modifiedPlayerQuest, gson, file, userFolder, questapi);
        }
    }


    public static void giveReward(Player player, PlayerQuest modifiedPlayerQuest, Gson gson, File file, Path userFolder, Path questapi) throws IOException {
        Path completedQuest = Paths.get(userFolder.toString(), "completed-quests");
        Path serverquests = Paths.get(questapi.toString(), "server-quests");

        if (modifiedPlayerQuest.isCompleted()) {
            for (File serverFile : serverquests.toFile().listFiles()) {
                if (serverFile.getName().equals("id_" + modifiedPlayerQuest.getId() + ".json")) {

                    FileReader serverQuestReader = new FileReader(serverFile);
                    ServerQuest serverQuest = gson.fromJson(serverQuestReader, ServerQuest.class);

                    switch (serverQuest.getReward_type()) {
                        case "item" -> {
                            for (int rewardIndex = 0; rewardIndex < serverQuest.getReward().size(); rewardIndex++) {
                                String valueItem = serverQuest.getReward().get(rewardIndex);
                                int quantity = serverQuest.getReward_quantity().get(rewardIndex);

                                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(valueItem));
                                ItemStack stack = new ItemStack(item, quantity);

                                player.addItem(stack);
                            }

                            break;
                        }

                        case "command" -> {
                            for (int rewardIndex = 0; rewardIndex < serverQuest.getReward().size(); rewardIndex++) {


                            }


                            break;
                        }
                    }

                    Files.move(file.toPath(), Paths.get(completedQuest.toString(), file.getName()));
                    serverQuestReader.close();
                }
            }
        }
    }
}


