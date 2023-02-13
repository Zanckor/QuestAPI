package dev.zanckor.mod;

import com.mojang.logging.LogUtils;
import dev.zanckor.example.ModExample;
import dev.zanckor.mod.common.network.QuestNetworkHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Mod(QuestApiMain.MOD_ID)
public class QuestApiMain {
    public static final String MOD_ID = "questapi";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static Path serverDirectory, questApi, playerData, serverQuests, serverDialogs;

    public QuestApiMain() {
        MinecraftForge.EVENT_BUS.register(this);

        QuestNetworkHandler.register();
        new ModExample();
    }


    public static Path getUserFolder(UUID playerUUID) {
        Path userFolder = Paths.get(playerData.toString(), playerUUID.toString());

        return userFolder;
    }

    public static Path getActiveQuest(Path userFolder) {
        Path activeQuest = Paths.get(userFolder.toString(), "active-quests");

        return activeQuest;
    }

    public static Path getCompletedQuest(Path userFolder) {
        Path completedQuest = Paths.get(userFolder.toString(), "completed-quests");

        return completedQuest;
    }

    public static Path getUncompletedQuest(Path userFolder) {
        Path uncompletedQuest = Paths.get(userFolder.toString(), "uncompleted-quests");

        return uncompletedQuest;
    }


    public static Path getReadDialogs(Path userFolder) {
        Path readDialogs = Paths.get(userFolder.toString(), "read-dialogs");

        return readDialogs;
    }
}
