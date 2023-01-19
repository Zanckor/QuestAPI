package com.zanckor.example.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zanckor.api.questregister.abstrac.AbstractQuest;
import com.zanckor.api.questregister.abstrac.PlayerQuest;
import com.zanckor.api.questregister.register.TemplateRegistry;
import com.zanckor.mod.QuestApiMain;
import com.zanckor.mod.network.SendQuestPacket;
import com.zanckor.mod.network.message.QuestDataPacket;
import com.zanckor.mod.util.Mathematic;
import com.zanckor.mod.util.Timer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.zanckor.api.EnumQuestType.*;
import static com.zanckor.mod.QuestApiMain.playerData;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class QuestEvent {

    @SubscribeEvent
    public static void killQuest(LivingDeathEvent e) {
        if (e.getEntity().level.isClientSide || !(e.getSource().getEntity() instanceof Player)) return;
        SendQuestPacket.TO_SERVER(new QuestDataPacket(KILL));
    }


    @SubscribeEvent
    public static void recollectPickUpQuest(PlayerEvent.ItemPickupEvent e) {
        if (e.getEntity().level.isClientSide) return;
        SendQuestPacket.TO_SERVER(new QuestDataPacket(RECOLLECT));
    }

    @SubscribeEvent
    public static void recollectCraftQuest(PlayerEvent.ItemCraftedEvent e) {
        if (e.getEntity().level.isClientSide) return;
        SendQuestPacket.TO_SERVER(new QuestDataPacket(RECOLLECT));
    }

    @SubscribeEvent
    public static void recollectCraftQuest(PlayerEvent.ItemSmeltedEvent e) {
        if (e.getEntity().level.isClientSide) return;
        SendQuestPacket.TO_SERVER(new QuestDataPacket(RECOLLECT));
    }


    @SubscribeEvent
    public static void interactWithNPC(PlayerInteractEvent.EntityInteract e) {
        if (e.getEntity().level.isClientSide || e.getHand() == InteractionHand.MAIN_HAND) return;
        SendQuestPacket.TO_SERVER(new QuestDataPacket(INTERACT_ENTITY));
    }

    @SubscribeEvent
    public static void failProtectEntity(LivingDeathEvent e) {
        if (e.getEntity().level.isClientSide) return;

        for (Player player : e.getEntity().getServer().getPlayerList().getPlayers()) {
            Path userFolder = Paths.get(playerData.toString(), player.getUUID().toString());
            Path activeQuest = Paths.get(userFolder.toString(), "active-quests");

            for (File file : activeQuest.toFile().listFiles()) {
                try {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();

                    FileReader reader = new FileReader(file);
                    PlayerQuest playerQuest = gson.fromJson(reader, PlayerQuest.class);
                    reader.close();

                    if (playerQuest == null || !playerQuest.getQuest_type().equals(PROTECT_ENTITY.toString())) return;
                    AbstractQuest quest = TemplateRegistry.getQuestTemplate(PROTECT_ENTITY);

                    for (String entityUUID : playerQuest.getQuest_target()) {
                        if (!e.getEntity().getUUID().toString().equals(entityUUID)) continue;

                        quest.handler(player, gson, file, playerQuest);
                    }

                } catch (IOException exception) {
                    QuestApiMain.LOGGER.error(exception.getMessage());
                }
            }
        }
    }

    @SubscribeEvent
    public static void eventQuests(TickEvent.PlayerTickEvent e) throws IOException {
        if (e.player.getServer() == null || e.player.getServer().getTickCount() % 20 != 0 || e.player.level.isClientSide)
            return;

        Path userFolder = Paths.get(playerData.toString(), e.player.getUUID().toString());
        Path activeQuest = Paths.get(userFolder.toString(), "active-quests");
        Path uncompletedQuest = Paths.get(userFolder.toString(), "uncompleted-quests");

        for (File file : activeQuest.toFile().listFiles()) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            FileReader reader = new FileReader(file);
            PlayerQuest playerQuest = gson.fromJson(reader, PlayerQuest.class);
            reader.close();

            if (playerQuest != null) {
                protectEntity(playerQuest, e.player);
                moveTo(playerQuest, e.player);
            }
        }
    }

    public static void moveTo(PlayerQuest playerQuest, Player player) {
        if (!playerQuest.getQuest_type().equals(MOVE_TO.toString()))
            return;

        Integer xCoord = Integer.valueOf(playerQuest.getQuest_target().get(0));
        Integer yCoord = Integer.valueOf(playerQuest.getQuest_target().get(1));
        Integer zCoord = Integer.valueOf(playerQuest.getQuest_target().get(2));
        Vec3 playerCoord = new Vec3(player.getBlockX(), player.getBlockY(), player.getBlockZ());

        if (Mathematic.numberBetween(playerCoord.x, xCoord - 10, xCoord + 10) && Mathematic.numberBetween(playerCoord.y, yCoord - 10, yCoord + 10) && Mathematic.numberBetween(playerCoord.z, zCoord - 10, zCoord + 10)) {
            SendQuestPacket.TO_SERVER(new QuestDataPacket(MOVE_TO));
        }
    }

    public static void protectEntity(PlayerQuest playerQuest, Player player) {
        if (playerQuest.getQuest_type().equals(PROTECT_ENTITY.toString()) && !playerQuest.getQuest_target().contains("entity")) {
            if (Timer.canUseWithCooldown(player.getUUID(), "id_" + playerQuest.getId(), playerQuest.getTimeLimitInSeconds())) {
                SendQuestPacket.TO_SERVER(new QuestDataPacket(PROTECT_ENTITY));
            }
        }
    }
}
