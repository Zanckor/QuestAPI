package com.zanckor.mod.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zanckor.api.database.LocateHash;
import com.zanckor.api.dialog.abstractdialog.DialogTemplate;
import com.zanckor.api.quest.ClientQuestBase;
import com.zanckor.api.quest.enumquest.EnumQuestType;
import com.zanckor.mod.QuestApiMain;
import com.zanckor.mod.util.MCUtil;
import com.zanckor.mod.util.Timer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.zanckor.api.database.LocateHash.registerQuestByID;
import static com.zanckor.api.database.LocateHash.registerQuestTypeLocation;
import static com.zanckor.mod.QuestApiMain.getCompletedQuest;
import static com.zanckor.mod.QuestApiMain.playerData;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvent {

    @SubscribeEvent
    public static void questWithTimer(TickEvent.PlayerTickEvent e) throws IOException {
        if (e.player.getServer() == null || e.player.getServer().getTickCount() % 20 != 0 || e.player.level.isClientSide)
            return;

        Path userFolder = Paths.get(playerData.toString(), e.player.getUUID().toString());
        Path activeQuest = QuestApiMain.getActiveQuest(userFolder);
        Path uncompletedQuest = QuestApiMain.getUncompletedQuest(userFolder);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();


        for (File file : activeQuest.toFile().listFiles()) {
            ClientQuestBase playerQuest = MCUtil.getJsonClientQuest(file, gson);

            if (playerQuest != null) {
                timer(playerQuest, e.player, file, gson, uncompletedQuest);
            }
        }
    }


    public static void timer(ClientQuestBase playerQuest, Player player, File file, Gson gson, Path uncompletedQuest) throws IOException {
        if (playerQuest == null) {
            QuestApiMain.LOGGER.error(player.getScoreboardName() + " has corrupted quest: " + file.getName());
            return;
        }

        Path userFolder = Paths.get(playerData.toFile().toString(), player.getUUID().toString());
        String questName = "id_" + playerQuest.getId() + ".json";

        if (!playerQuest.isCompleted() && playerQuest.hasTimeLimit() && Timer.canUseWithCooldown(player.getUUID(), "id_" + playerQuest.getId(), playerQuest.getTimeLimitInSeconds())) {

            FileWriter writer = new FileWriter(file);
            playerQuest.setCompleted(true);

            gson.toJson(playerQuest, writer);
            writer.close();

            LocateHash.movePathQuest(playerQuest.getId(), file.toPath().toAbsolutePath(),  Paths.get(getCompletedQuest(userFolder).toString(), questName), EnumQuestType.valueOf(playerQuest.getQuest_type()));
            Files.move(file.toPath(), Paths.get(uncompletedQuest.toString(), file.getName()));
        }
    }

    @SubscribeEvent
    public static void loadHashMaps(PlayerEvent.PlayerLoggedInEvent e) throws IOException {
        Path userFolder = QuestApiMain.getUserFolder(e.getEntity().getUUID());
        Path activeQuest = QuestApiMain.getActiveQuest(userFolder);
        Path completedQuest = QuestApiMain.getCompletedQuest(userFolder);
        Path uncompletedQuest = QuestApiMain.getUncompletedQuest(userFolder);

        Path[] paths = {activeQuest, completedQuest, uncompletedQuest};

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        for (Path path : paths) {
            for (File file : path.toFile().listFiles()) {
                ClientQuestBase playerQuest = MCUtil.getJsonClientQuest(file, gson);
                if (playerQuest == null) continue;

                registerQuestTypeLocation(EnumQuestType.valueOf(playerQuest.getQuest_type()), file.toPath().toAbsolutePath());
                registerQuestByID(playerQuest.getId(), file.toPath().toAbsolutePath());
            }
        }


        for (File file : QuestApiMain.serverDialogs.toFile().listFiles()) {
            FileReader reader = new FileReader(file);
            DialogTemplate dialogTemplate = gson.fromJson(reader, DialogTemplate.class);
            reader.close();

            DialogTemplate.registerDialogLocation(dialogTemplate.getGlobal_id(), file.toPath().toAbsolutePath());
        }
    }
}
