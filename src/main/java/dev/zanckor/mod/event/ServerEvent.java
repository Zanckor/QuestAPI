package dev.zanckor.mod.event;

import com.google.gson.Gson;
import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.dialog.abstractdialog.DialogTemplate;
import dev.zanckor.api.quest.ClientQuestBase;
import dev.zanckor.example.enumregistry.enumquest.EnumQuestType;
import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.util.MCUtil;
import dev.zanckor.mod.util.Timer;
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

    @SubscribeEvent
    public static void questWithTimer(TickEvent.PlayerTickEvent e) throws IOException {
        if (e.player.getServer() == null || e.player.getServer().getTickCount() % 20 != 0 || e.player.level.isClientSide)
            return;

        Path activeQuest = QuestApiMain.getActiveQuest(QuestApiMain.getUserFolder(e.player.getUUID()));
        Path uncompletedQuest = QuestApiMain.getUncompletedQuest(QuestApiMain.getUserFolder(e.player.getUUID()));

        for (File file : activeQuest.toFile().listFiles()) {
            ClientQuestBase playerQuest = MCUtil.getJsonClientQuest(file);

            if (playerQuest != null) {
                timer(playerQuest, e.player, file, MCUtil.gson(), uncompletedQuest);
            }
        }
    }


    public static void timer(ClientQuestBase playerQuest, Player player, File file, Gson gson, Path uncompletedQuest) throws IOException {
        if (playerQuest == null) {
            QuestApiMain.LOGGER.error(player.getScoreboardName() + " has corrupted quest: " + file.getName());
            return;
        }

        if (!playerQuest.isCompleted() && playerQuest.hasTimeLimit() && Timer.canUseWithCooldown(player.getUUID(), playerQuest.getId(), playerQuest.getTimeLimitInSeconds())) {
            FileWriter writer = new FileWriter(file);
            playerQuest.setCompleted(true);

            gson.toJson(playerQuest, writer);
            writer.close();

            Path uncompletedPath = Paths.get(uncompletedQuest.toString(), file.getName());

            Files.move(file.toPath(), uncompletedPath);
            LocateHash.movePathQuest(playerQuest.getId(), uncompletedPath, EnumQuestType.valueOf(playerQuest.getQuest_type()));
        }
    }

    @SubscribeEvent
    public static void uncompletedQuestOnLogOut(PlayerEvent.PlayerLoggedOutEvent e) throws IOException {
        Path activeQuest = QuestApiMain.getActiveQuest(QuestApiMain.getUserFolder(e.getEntity().getUUID()));
        Path uncompletedQuest = QuestApiMain.getUncompletedQuest(QuestApiMain.getUserFolder(e.getEntity().getUUID()));
        List<EnumQuestType> questWithTimer = new ArrayList<>();

        questWithTimer.add(EnumQuestType.PROTECT_ENTITY);

        for (File file : activeQuest.toFile().listFiles()) {
            ClientQuestBase playerQuest = MCUtil.getJsonClientQuest(file);

            if (playerQuest != null && playerQuest.hasTimeLimit() || questWithTimer.contains(EnumQuestType.valueOf(playerQuest.getQuest_type()))) {
                FileWriter writer = new FileWriter(file);
                playerQuest.setCompleted(true);

                MCUtil.gson().toJson(playerQuest, writer);
                writer.close();

                Path uncompletedPath = Paths.get(uncompletedQuest.toString(), file.getName());

                Files.move(file.toPath(), uncompletedPath);
                LocateHash.movePathQuest(playerQuest.getId(), uncompletedPath, EnumQuestType.valueOf(playerQuest.getQuest_type()));
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
            for (File file : path.toFile().listFiles()) {
                ClientQuestBase playerQuest = MCUtil.getJsonClientQuest(file);
                if (playerQuest == null) continue;

                LocateHash.registerQuestTypeLocation(EnumQuestType.valueOf(playerQuest.getQuest_type()), file.toPath().toAbsolutePath());
                LocateHash.registerQuestByID(playerQuest.getId(), file.toPath().toAbsolutePath());
            }
        }


        for (File file : QuestApiMain.serverDialogs.toFile().listFiles()) {
            FileReader reader = new FileReader(file);
            DialogTemplate dialogTemplate = MCUtil.gson().fromJson(reader, DialogTemplate.class);
            reader.close();

            LocateHash.registerDialogLocation(file.getName(), file.toPath().toAbsolutePath());
        }
    }
}
