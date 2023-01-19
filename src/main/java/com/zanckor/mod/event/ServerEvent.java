package com.zanckor.mod.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zanckor.api.questregister.abstrac.PlayerQuest;
import com.zanckor.mod.QuestApiMain;
import com.zanckor.mod.util.Timer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.zanckor.mod.QuestApiMain.playerData;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvent {

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
                timer(playerQuest, e.player, file, gson, uncompletedQuest);
            }
        }
    }


    public static void timer(PlayerQuest playerQuest, Player player, File file, Gson gson, Path uncompletedQuest) throws IOException {
        if (playerQuest == null) {
            QuestApiMain.LOGGER.error(player.getScoreboardName() + " has corrupted quest: " + file.getName());
            return;
        }

        if (!playerQuest.isCompleted() && playerQuest.hasTimeLimit && Timer.canUseWithCooldown(player.getUUID(), "id_" + playerQuest.getId(), playerQuest.getTimeLimitInSeconds())) {
            FileWriter writer = new FileWriter(file);
            playerQuest.setCompleted(true);

            gson.toJson(playerQuest, writer);
            writer.close();

            Files.move(file.toPath(), Paths.get(uncompletedQuest.toString(), file.getName()));
        }
    }
}
