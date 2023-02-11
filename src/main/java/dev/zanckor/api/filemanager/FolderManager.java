package dev.zanckor.api.filemanager;

import dev.zanckor.mod.QuestApiMain;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static dev.zanckor.mod.QuestApiMain.*;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FolderManager {

    /**
     * Each time that server starts <code> serverFolderManager </code> is called to create base folders
     * Each time a player joins the server, <code> playerFolderManager </code> is called to create his data folders
     */

    @SubscribeEvent
    public static void serverFolderManager(ServerAboutToStartEvent e) {
        Path serverDirectory = e.getServer().getServerDirectory().toPath();

        File questApi = new File(serverDirectory.toString(), "quest-api");
        File playerData = new File(questApi.toString(), "player-data");
        File serverQuests = new File(questApi.toString(), "server-quests");
        File serverDialogs = new File(questApi.toString(), "server-dialogs");

        File[] paths = {questApi, playerData, serverQuests, serverDialogs};

        for (File file : paths) {
            if (!file.exists()) {
                file.mkdirs();
            }
        }

        QuestApiMain.serverDirectory = serverDirectory;
        QuestApiMain.questApi = questApi.toPath();
        QuestApiMain.playerData = playerData.toPath();
        QuestApiMain.serverQuests = serverQuests.toPath();
        QuestApiMain.serverDialogs = serverDialogs.toPath();
    }


    @SubscribeEvent
    public static void playerFolderManager(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntity().level.isClientSide || !(e.getEntity() instanceof Player)) return;

        Path userFolder = Paths.get(playerData.toString(), e.getEntity().getUUID().toString());

        if (!userFolder.toFile().exists()) {
            getActiveQuest(userFolder).toFile().mkdirs();
            getCompletedQuest(userFolder).toFile().mkdirs();
            getUncompletedQuest(userFolder).toFile().mkdirs();
            getReadDialogs(userFolder).toFile().mkdirs();
        }
    }
}
