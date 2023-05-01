package dev.zanckor.mod.common.network.handler;

import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.dialog.abstractdialog.AbstractDialogOption;
import dev.zanckor.api.filemanager.dialog.codec.NPCConversation;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractGoal;
import dev.zanckor.api.filemanager.quest.codec.user.UserGoal;
import dev.zanckor.api.filemanager.quest.codec.user.UserQuest;
import dev.zanckor.api.filemanager.quest.register.QuestTemplateRegistry;
import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.common.util.GsonManager;
import dev.zanckor.mod.common.util.MCUtil;
import dev.zanckor.mod.common.util.Timer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static dev.zanckor.mod.QuestApiMain.playerData;

public class ServerHandler {

    public static void addQuest(Player player, Enum optionType, int optionID) {
        String dialogGlobalID = LocateHash.currentGlobalDialog.get(player);

        Path path = LocateHash.getDialogLocation(dialogGlobalID);
        File dialogFile = path.toFile();
        AbstractDialogOption dialogTemplate = QuestTemplateRegistry.getDialogTemplate(optionType);

        try {
            NPCConversation dialog = (NPCConversation) GsonManager.getJsonClass(dialogFile, NPCConversation.class);

            dialogTemplate.handler(player, dialog, optionID, null);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void questHandler(Enum questType, ServerPlayer player, LivingEntity entity) throws IOException {
        AbstractGoal quest = QuestTemplateRegistry.getQuestTemplate(questType);
        List<Path> questTypeLocation = LocateHash.getQuestTypeLocation(questType);

        if (quest == null || questTypeLocation == null) return;

        for (int i = 0; i < questTypeLocation.size(); i++) {
            File file = questTypeLocation.get(i).toAbsolutePath().toFile();
            UserQuest userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
            if (userQuest == null || userQuest.isCompleted()) continue;

            for (int indexGoals = 0; indexGoals < userQuest.getQuestGoals().size(); indexGoals++) {
                userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
                if (userQuest == null) return;

                UserGoal questGoal = userQuest.getQuestGoals().get(indexGoals);

                if (questGoal.getType().equals(questType.toString())) {
                    quest.handler(player, entity, GsonManager.gson, file, userQuest, indexGoals, questType);
                }
            }
        }
    }

    public static void requestDialog(ServerPlayer player, int optionID, Enum optionType, UUID npcUuid) {
        String globalDialogID = LocateHash.currentGlobalDialog.get(player);

        Path path = LocateHash.getDialogLocation(globalDialogID);
        File dialogFile = path.toFile();
        AbstractDialogOption dialogTemplate = QuestTemplateRegistry.getDialogTemplate(optionType);

        try {
            NPCConversation dialog = (NPCConversation) GsonManager.getJsonClass(dialogFile, NPCConversation.class);
            dialogTemplate.handler(player, dialog, optionID, MCUtil.getEntityByUUID(player.getLevel(), npcUuid));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void questTimer(ServerLevel level) {

        for (Player player : level.players()) {
            Path userFolder = Paths.get(playerData.toString(), player.getUUID().toString());

            for (File file : userFolder.toFile().listFiles()) {
                try {
                    UserQuest userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
                    if (userQuest.isCompleted() || !userQuest.hasTimeLimit() || !Timer.canUseWithCooldown(player.getUUID(), userQuest.getId(), userQuest.getTimeLimitInSeconds()))
                        return;

                    userQuest.setCompleted(true);
                    GsonManager.writeJson(file, userQuest);

                } catch (IOException exception) {
                    QuestApiMain.LOGGER.error(exception.getMessage());
                }
            }
        }
    }
}
