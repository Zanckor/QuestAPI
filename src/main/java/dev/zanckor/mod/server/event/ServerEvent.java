package dev.zanckor.mod.server.event;

import com.google.gson.Gson;
import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.dialog.ServerDialog;
import dev.zanckor.api.filemanager.quest.UserQuest;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestType;
import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.common.util.GsonManager;
import dev.zanckor.mod.common.util.Timer;
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
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvent {

    /*
     * TODO: Add auto-save quest's timer so on logout it wont lose the quest, jut will freeze the timer.
     */

    @SubscribeEvent
    public static void questWithTimer(TickEvent.PlayerTickEvent e) throws IOException {
        if (e.player.getServer() == null || e.player.getServer().getTickCount() % 20 != 0 || e.player.level.isClientSide)
            return;

        Path activeQuest = QuestApiMain.getActiveQuest(QuestApiMain.getUserFolder(e.player.getUUID()));
        Path uncompletedQuest = QuestApiMain.getUncompletedQuest(QuestApiMain.getUserFolder(e.player.getUUID()));

        for (File file : activeQuest.toFile().listFiles()) {
            UserQuest userQuest = (UserQuest) GsonManager.getJson(file, UserQuest.class);

            if (userQuest != null) {
                timer(userQuest, e.player, file, GsonManager.gson(), uncompletedQuest);
            }
        }
    }


    public static void timer(UserQuest userQuest, Player player, File file, Gson gson, Path uncompletedQuest) throws IOException {
        if (userQuest == null) {
            QuestApiMain.LOGGER.error(player.getScoreboardName() + " has corrupted quest: " + file.getName());
            return;
        }

        if (!userQuest.isCompleted() && userQuest.hasTimeLimit() && Timer.canUseWithCooldown(player.getUUID(), userQuest.getId(), userQuest.getTimeLimitInSeconds())) {
            FileWriter writer = new FileWriter(file);
            userQuest.setCompleted(true);

            gson.toJson(userQuest, writer);
            writer.close();

            Path uncompletedPath = Paths.get(uncompletedQuest.toString(), file.getName());

            Files.move(file.toPath(), uncompletedPath);
            LocateHash.movePathQuest(userQuest.getId(), uncompletedPath, EnumQuestType.valueOf(userQuest.getQuest_type()));
        }
    }

    @SubscribeEvent
    public static void uncompletedQuestOnLogOut(PlayerEvent.PlayerLoggedOutEvent e) throws IOException {
        Path activeQuest = QuestApiMain.getActiveQuest(QuestApiMain.getUserFolder(e.getEntity().getUUID()));
        Path uncompletedQuest = QuestApiMain.getUncompletedQuest(QuestApiMain.getUserFolder(e.getEntity().getUUID()));
        List<EnumQuestType> questWithTimer = new ArrayList<>();

        questWithTimer.add(EnumQuestType.PROTECT_ENTITY);

        for (File file : activeQuest.toFile().listFiles()) {
            UserQuest userQuest = (UserQuest) GsonManager.getJson(file, UserQuest.class);

            if (userQuest != null && userQuest.hasTimeLimit()) {
                if (questWithTimer.contains(EnumQuestType.valueOf(userQuest.getQuest_type()))) {
                    FileWriter writer = new FileWriter(file);
                    userQuest.setCompleted(true);

                    GsonManager.gson().toJson(userQuest, writer);
                    writer.close();

                    Path uncompletedPath = Paths.get(uncompletedQuest.toString(), file.getName());

                    Files.move(file.toPath(), uncompletedPath);
                    LocateHash.movePathQuest(userQuest.getId(), uncompletedPath, EnumQuestType.valueOf(userQuest.getQuest_type()));
                }
            }
        }
    }


    @SubscribeEvent
    public static void loadHashMaps(PlayerEvent.PlayerLoggedInEvent e) throws IOException {
        Path userFolder = QuestApiMain.getUserFolder(e.getEntity().getUUID());
        Path activeQuest = QuestApiMain.getActiveQuest(userFolder);
        Path completedQuest = QuestApiMain.getCompletedQuest(userFolder);
        Path uncompletedQuest = QuestApiMain.getUncompletedQuest(userFolder);

        Path[] paths = {activeQuest, completedQuest, uncompletedQuest};

        for (Path path : paths) {
            if (path.toFile().listFiles() != null) {

                for (File file : path.toFile().listFiles()) {
                    UserQuest userQuest = (UserQuest) GsonManager.getJson(file, UserQuest.class);
                    if (userQuest == null) continue;

                    LocateHash.registerQuestTypeLocation(EnumQuestType.valueOf(userQuest.getQuest_type()), file.toPath().toAbsolutePath());
                    LocateHash.registerQuestByID(userQuest.getId(), file.toPath().toAbsolutePath());
                }
            }
        }


        if (QuestApiMain.serverDialogs.toFile().listFiles() != null) {
            for (File file : QuestApiMain.serverDialogs.toFile().listFiles()) {
                FileReader reader = new FileReader(file);
                ServerDialog dialogTemplate = GsonManager.gson().fromJson(reader, ServerDialog.class);
                reader.close();

                LocateHash.registerDialogLocation(file.getName(), file.toPath().toAbsolutePath());
            }
        }
    }
}
