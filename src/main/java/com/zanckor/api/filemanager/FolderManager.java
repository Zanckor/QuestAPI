package com.zanckor.api.filemanager;

import com.zanckor.mod.QuestApiMain;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.zanckor.mod.QuestApiMain.*;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FolderManager {

    @SubscribeEvent
    public static void serverFolderManager(ServerAboutToStartEvent e) {
        Path serverDirectory = e.getServer().getServerDirectory().toPath();

        File questApi = new File(serverDirectory.toString(), "quest-api");
        File playerData = new File(questApi.toString(), "player-data");
        File serverQuests = new File(questApi.toString(), "server-quests");

        File[] paths = {questApi, playerData, serverQuests};

        for (File file : paths) {
            if (!file.exists()) {
                file.mkdirs();
            }
        }

        QuestApiMain.serverDirectory = serverDirectory;
        QuestApiMain.questApi = questApi.toPath();
        QuestApiMain.playerData = playerData.toPath();
        QuestApiMain.serverQuests = serverQuests.toPath();
    }


    @SubscribeEvent
    public static void playerFolderManager(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntity().level.isClientSide || !(e.getEntity() instanceof Player)) return;

        Path userFolder = Paths.get(playerData.toString(), e.getEntity().getUUID().toString());

        if (!userFolder.toFile().exists()) {
            getActiveQuest(userFolder).toFile().mkdirs();
            getCompletedQuest(userFolder).toFile().mkdirs();
            getUncompletedQuest(userFolder).toFile().mkdirs();
        }
    }
}
