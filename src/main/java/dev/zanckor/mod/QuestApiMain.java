package dev.zanckor.mod;

import com.mojang.logging.LogUtils;
import dev.zanckor.example.ModExample;
import dev.zanckor.mod.common.config.client.ScreenConfig;
import dev.zanckor.mod.common.network.NetworkHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);

        NetworkHandler.register();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ScreenConfig.SPEC, "questapi-client.toml");
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
