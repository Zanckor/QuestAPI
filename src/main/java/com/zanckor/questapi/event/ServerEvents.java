package com.zanckor.questapi.event;

import com.zanckor.questapi.QuestApi;
import com.zanckor.questapi.network.SendPacket;
import com.zanckor.questapi.network.messages.QuestData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

@Mod.EventBusSubscriber(modid = QuestApi.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvents {

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

        if (!userFolder.toFile().exists()) {
            activeQuest.toFile().mkdirs();
            completedQuest.toFile().mkdirs();
        }
    }


    @SubscribeEvent
    public static void killQuest(LivingDeathEvent e) {
        if (e.getEntity().level.isClientSide || !(e.getSource().getEntity() instanceof Player)) return;
        SendPacket.TO_SERVER(new QuestData("kill"));
    }
}
