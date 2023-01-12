package com.zanckor.questapi.network.messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zanckor.questapi.QuestApi;
import com.zanckor.questapi.createQuest.ServerQuest;
import com.zanckor.questapi.createQuest.kill.ClientKillQuest;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            Path serverquests = Paths.get(questapi.toString(), "server-quests");

            for (File file : userFolder.toFile().listFiles()) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();

                try {
                    FileReader reader = new FileReader(file);
                    ClientKillQuest playerQuest = gson.fromJson(reader, ClientKillQuest.class);
                    reader.close();

                    for(int targetIndex = 0; targetIndex < playerQuest.getTarget_entity().size() - 1; targetIndex++) {
                        if (playerQuest.completed || !(playerQuest.getTarget_entity().get(targetIndex).equals(player.getLastHurtMob().getType().getDescriptionId())))
                            return;

                        FileWriter writer = new FileWriter(file);

                        gson.toJson(playerQuest.incrementProgress(playerQuest, targetIndex), writer);
                        writer.close();


                        if (playerQuest.getTarget_current_quantity().get(targetIndex) + 1 >= playerQuest.getTarget_quantity().get(targetIndex)) {
                            for (File serverFile : serverquests.toFile().listFiles()) {
                                if (serverFile.getName().equals("id_" + playerQuest.getId() + ".json")) {

                                    FileReader serverQuestReader = new FileReader(serverFile);
                                    ServerQuest serverQuest = gson.fromJson(serverQuestReader, ServerQuest.class);

                                    switch (serverQuest.getReward_type()) {
                                        case "item" -> {
                                            for (int rewardIndex = 0; rewardIndex < serverQuest.getReward().size() - 1; rewardIndex++) {
                                                String valueItem = serverQuest.getReward().get(rewardIndex);
                                                int quantity = serverQuest.getReward_quantity().get(rewardIndex);

                                                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(valueItem));
                                                ItemStack stack = new ItemStack(item, quantity);

                                                player.addItem(stack);

                                                break;
                                            }
                                        }
                                    }

                                    reader.close();
                                }
                            }
                        }
                    }

                } catch (IOException e) {
                    QuestApi.LOGGER.error("File reader/writer error");
                }
            }
        });

        ctx.get().setPacketHandled(true);

    }
}


