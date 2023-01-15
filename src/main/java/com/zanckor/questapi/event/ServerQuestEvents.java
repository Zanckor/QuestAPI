package com.zanckor.questapi.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zanckor.questapi.QuestApi;
import com.zanckor.questapi.createQuest.PlayerQuest;
import com.zanckor.questapi.network.SendQuestPacket;
import com.zanckor.questapi.network.messages.QuestData;
import com.zanckor.questapi.utils.Maths;
import com.zanckor.questapi.utils.QuestTimers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = QuestApi.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerQuestEvents {

    @SubscribeEvent
    public static void createFolder(ServerAboutToStartEvent e) {
        Path serverDirectory = e.getServer().getServerDirectory().toPath();

        File questapi = new File(serverDirectory.toString(), "quest-api");
        File playerdata = new File(questapi.toString(), "player-data");
        File serverquests = new File(questapi.toString(), "server-quests");

        if (!questapi.exists()) {
            questapi.mkdir();
        }

        if (!playerdata.exists()) {
            playerdata.mkdir();
        }

        if (!serverquests.exists()) {
            serverquests.mkdir();
        }
    }


    @SubscribeEvent
    public static void createPlayerDataFolder(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntity().level.isClientSide || !(e.getEntity() instanceof Player)) return;

        Path serverDirectory = e.getEntity().level.getServer().getServerDirectory().toPath();

        Path questapi = Paths.get(serverDirectory.toString(), "quest-api");
        Path playerdata = Paths.get(questapi.toString(), "player-data");
        Path userFolder = Paths.get(playerdata.toString(), e.getEntity().getUUID().toString());
        Path activeQuest = Paths.get(userFolder.toString(), "active-quests");
        Path completedQuest = Paths.get(userFolder.toString(), "completed-quests");
        Path uncompletedQuest = Paths.get(userFolder.toString(), "uncompleted-quests");

        if (!userFolder.toFile().exists()) {
            activeQuest.toFile().mkdirs();
            completedQuest.toFile().mkdirs();
            uncompletedQuest.toFile().mkdirs();
        }
    }


    @SubscribeEvent
    public static void killQuest(LivingDeathEvent e) {
        if (e.getEntity().level.isClientSide || !(e.getSource().getEntity() instanceof Player)) return;
        SendQuestPacket.TO_SERVER(new QuestData("kill"));
    }


    @SubscribeEvent
    public static void recollectPickUpQuest(PlayerEvent.ItemPickupEvent e) {
        if (e.getEntity().level.isClientSide || !(e.getEntity() instanceof Player)) return;
        SendQuestPacket.TO_SERVER(new QuestData("recollect"));
    }

    @SubscribeEvent
    public static void recollectCraftQuest(PlayerEvent.ItemCraftedEvent e) {
        if (e.getEntity().level.isClientSide || !(e.getEntity() instanceof Player)) return;
        SendQuestPacket.TO_SERVER(new QuestData("recollect"));
    }

    @SubscribeEvent
    public static void recollectCraftQuest(PlayerEvent.ItemSmeltedEvent e) {
        if (e.getEntity().level.isClientSide || !(e.getEntity() instanceof Player)) return;
        SendQuestPacket.TO_SERVER(new QuestData("recollect"));
    }


    @SubscribeEvent
    public static void interactWithNPC(PlayerInteractEvent.EntityInteract e) {
        if (e.getEntity().level.isClientSide || e.getHand() == InteractionHand.MAIN_HAND) return;

        SendQuestPacket.TO_SERVER(new QuestData("npc_interact"));
    }


    @SubscribeEvent
    public static void questTimerAndQuests(TickEvent.ServerTickEvent e) throws IOException {
        if (e.getServer().getTickCount() % 20 != 0) return;

        for (Player player : e.getServer().getPlayerList().getPlayers()) {
            Path serverDirectory = e.getServer().getServerDirectory().toPath();

            Path questapi = Paths.get(serverDirectory.toString(), "quest-api");
            Path playerdata = Paths.get(questapi.toString(), "player-data");
            Path userFolder = Paths.get(playerdata.toString(), player.getUUID().toString());
            Path activeQuest = Paths.get(userFolder.toString(), "active-quests");
            Path uncompletedQuest = Paths.get(userFolder.toString(), "uncompleted-quests");

            for (File file : activeQuest.toFile().listFiles()) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();

                try {
                    FileReader reader = new FileReader(file);
                    PlayerQuest playerQuest = gson.fromJson(reader, PlayerQuest.class);
                    reader.close();

                    timer(playerQuest, player, file, gson, uncompletedQuest);

                    protectEntity(playerQuest, player);

                    reachCoord(playerQuest, player);

                } catch (IOException exception) {
                    QuestApi.LOGGER.error("File reader/writer error");
                }
            }
        }
    }


    public static void timer(PlayerQuest playerQuest, Player player, File file, Gson gson, Path uncompletedQuest) throws IOException {
        if (playerQuest == null) {
            QuestApi.LOGGER.error(player.getScoreboardName() + " has corrupted quest: " + file.getName());
            return;
        }

        if (!playerQuest.isCompleted() && playerQuest.hasTimeLimit && QuestTimers.canUseWithCooldown(player.getUUID(), "id_" + playerQuest.getId(), playerQuest.getTimeLimitInSeconds())) {
            FileWriter writer = new FileWriter(file);
            playerQuest.setCompleted(true);

            gson.toJson(playerQuest, writer);
            writer.close();

            Files.move(file.toPath(), Paths.get(uncompletedQuest.toString(), file.getName()));
        }
    }


    public static void reachCoord(PlayerQuest playerQuest, Player player) throws IOException {
        if (playerQuest.getQuest_target().get(0).contains("entity") || !playerQuest.getQuest_type().equals("reach_coord"))
            return;

        Integer xCoord = Integer.valueOf(playerQuest.getQuest_target().get(0));
        Integer yCoord = Integer.valueOf(playerQuest.getQuest_target().get(1));
        Integer zCoord = Integer.valueOf(playerQuest.getQuest_target().get(2));
        Vec3 playerCoord = new Vec3(player.getBlockX(), player.getBlockY(), player.getBlockZ());

        if (Maths.numberBetween(playerCoord.x, xCoord - 10, xCoord + 10) && Maths.numberBetween(playerCoord.y, yCoord - 10, yCoord + 10) && Maths.numberBetween(playerCoord.z, zCoord - 10, zCoord + 10)) {
            SendQuestPacket.TO_SERVER(new QuestData("reach_coord"));
        }
    }


    public static void protectEntity(PlayerQuest playerQuest, Player player) {
        if (playerQuest.getQuest_type().equals("protect_entity") && !playerQuest.getQuest_target().contains("entity")) {
            UUID entityUUID = UUID.fromString(playerQuest.getQuest_target().get(0));

            Entity entity = player.getServer().overworld().getEntity(entityUUID);

            if (entity != null && entity.isAlive() && QuestTimers.canUseWithCooldown(player.getUUID(), "id_" + playerQuest.getId(), playerQuest.getTimeLimitInSeconds())) {
                SendQuestPacket.TO_SERVER(new QuestData("protect_entity"));
                entity.remove(Entity.RemovalReason.DISCARDED);
            } else if (entity == null || !entity.isAlive()) {
                SendQuestPacket.TO_SERVER(new QuestData("protect_entity"));
            }
        }
    }
}
