package dev.zanckor.mod.server.event;

import com.google.gson.Gson;
import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.dialog.codec.ServerDialog;
import dev.zanckor.api.filemanager.npc.entity_type_tag.codec.EntityTypeTagDialog;
import dev.zanckor.api.filemanager.npc.entity_type_tag.codec.EntityTypeTagDialog.EntityTypeTagDialogCondition;
import dev.zanckor.api.filemanager.npc.entity_type_tag.codec.EntityTypeTagDialog.EntityTypeTagDialogCondition.EntityTypeTagDialogNBT;
import dev.zanckor.api.filemanager.quest.codec.UserQuest;
import dev.zanckor.example.client.event.StartDialog;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestType;
import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.common.util.GsonManager;
import dev.zanckor.mod.common.util.MCUtil;
import dev.zanckor.mod.common.util.Timer;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
import java.util.Map;

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
            UserQuest userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);

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

            for (int indexGoals = 0; indexGoals < userQuest.getQuestGoals().size(); indexGoals++) {
                UserQuest.QuestGoal questGoal = userQuest.getQuestGoals().get(indexGoals);

                LocateHash.movePathQuest(userQuest.getId(), uncompletedPath, EnumQuestType.valueOf(questGoal.getType()));
            }
        }
    }

    @SubscribeEvent
    public static void uncompletedQuestOnLogOut(PlayerEvent.PlayerLoggedOutEvent e) throws IOException {
        Path activeQuest = QuestApiMain.getActiveQuest(QuestApiMain.getUserFolder(e.getEntity().getUUID()));
        Path uncompletedQuest = QuestApiMain.getUncompletedQuest(QuestApiMain.getUserFolder(e.getEntity().getUUID()));

        for (File file : activeQuest.toFile().listFiles()) {
            UserQuest userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
            if (userQuest == null || !userQuest.hasTimeLimit()) continue;

            for (int indexGoals = 0; indexGoals < userQuest.getQuestGoals().size(); indexGoals++) {
                UserQuest.QuestGoal questGoal = userQuest.getQuestGoals().get(indexGoals);

                FileWriter writer = new FileWriter(file);
                userQuest.setCompleted(true);

                GsonManager.gson().toJson(userQuest, writer);
                writer.close();

                Path uncompletedPath = Paths.get(uncompletedQuest.toString(), file.getName());

                Files.move(file.toPath(), uncompletedPath);
                LocateHash.movePathQuest(userQuest.getId(), uncompletedPath, EnumQuestType.valueOf(questGoal.getType()));
            }
        }
    }


    @SubscribeEvent
    public static void loadHashMaps(PlayerEvent.PlayerLoggedInEvent e) throws IOException {
        Path userFolder = QuestApiMain.getUserFolder(e.getEntity().getUUID());
        Path activeQuest = QuestApiMain.getActiveQuest(userFolder);
        Path completedQuest = QuestApiMain.getCompletedQuest(userFolder);
        Path uncompletedQuest = QuestApiMain.getUncompletedQuest(userFolder);

        Path[] questPaths = {activeQuest, completedQuest, uncompletedQuest};

        for (Path path : questPaths) {
            if (path.toFile().listFiles() != null) {

                for (File file : path.toFile().listFiles()) {
                    UserQuest userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);
                    if (userQuest == null) continue;

                    for (int indexGoals = 0; indexGoals < userQuest.getQuestGoals().size(); indexGoals++) {
                        UserQuest.QuestGoal questGoal = userQuest.getQuestGoals().get(indexGoals);
                        LocateHash.registerQuestTypeLocation(EnumQuestType.valueOf(questGoal.getType()), file.toPath().toAbsolutePath());
                    }

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


    @SubscribeEvent
    public static void loadDialogPerEntityType(PlayerInteractEvent.EntityInteract e) throws IOException {
        Player player = e.getEntity();
        Entity target = e.getTarget();
        String targetEntityType = EntityType.getKey(target.getType()).toString();

        List<String> dialogPerEntityType = LocateHash.getDialogPerEntityType(targetEntityType);
        if (!player.level.isClientSide && dialogPerEntityType != null && e.getHand().equals(InteractionHand.MAIN_HAND) && !openVanillaMenu(player)) {
            String selectedDialog = target.getPersistentData().getString("dialog");

            if (target.getPersistentData().get("dialog") == null) {
                int selectedInteger = MCUtil.randomBetween(0, dialogPerEntityType.size());
                selectedDialog = dialogPerEntityType.get(selectedInteger);

                target.getPersistentData().putString("dialog", selectedDialog);
            }

            StartDialog.loadDialog(player, selectedDialog, target);
        }
    }

    @SubscribeEvent
    public static void loadDialogPerCompoundTag(PlayerInteractEvent.EntityInteract e) throws IOException {
        Player player = e.getEntity();
        Entity target = e.getTarget();
        List<String> dialogs = new ArrayList<>();

        if (!player.level.isClientSide && e.getHand().equals(InteractionHand.MAIN_HAND) && !openVanillaMenu(player)) {

            for (Map.Entry<String, File> entry : LocateHash.dialogPerCompoundTag.entrySet()) {
                CompoundTag entityNBT = NbtPredicate.getEntityTagToCompare(target);
                File value = entry.getValue();
                EntityTypeTagDialog entityTypeDialog = (EntityTypeTagDialog) GsonManager.getJsonClass(value, EntityTypeTagDialog.class);

                conditions:
                for (EntityTypeTagDialogCondition conditions : entityTypeDialog.getConditions()) {
                    boolean tagCompare;

                    switch (conditions.getLogic_gate()) {
                        case OR: {
                            for (EntityTypeTagDialogNBT nbt : conditions.getNbt()) {
                                if (entityNBT.get(nbt.getTag()) == null) {
                                    tagCompare = false;
                                    continue;
                                }

                                tagCompare = entityNBT.get(nbt.getTag()).getAsString().contains(nbt.getValue());

                                if (tagCompare) {
                                    dialogs.addAll(conditions.getDialog_list());

                                    continue conditions;
                                }
                            }
                            break;
                        }

                        case AND: {
                            boolean shouldAddDialogList = false;

                            for (EntityTypeTagDialogNBT nbt : conditions.getNbt()) {
                                if (entityNBT.get(nbt.getTag()) != null) {
                                    tagCompare = entityNBT.get(nbt.getTag()).getAsString().contains(nbt.getValue());
                                } else {
                                    tagCompare = false;
                                }

                                shouldAddDialogList = tagCompare;

                                if (!tagCompare) break;
                            }

                            if (shouldAddDialogList) {
                                dialogs.addAll(conditions.getDialog_list());
                            }

                            break;
                        }
                    }
                }
            }

            if (!dialogs.isEmpty()) {
                e.setCanceled(true);


                String selectedDialog = target.getPersistentData().getString("dialog");

                if (target.getPersistentData().get("dialog") == null && !dialogs.isEmpty()) {
                    int selectedInteger = MCUtil.randomBetween(0, dialogs.size());
                    selectedDialog = dialogs.get(selectedInteger).toString();

                    target.getPersistentData().putString("dialog", selectedDialog);
                }

                StartDialog.loadDialog(player, selectedDialog, target);
            }
        }
    }


    public static boolean openVanillaMenu(Player player){
        if (player.isShiftKeyDown()) {
            player.setShiftKeyDown(false);
            return true;
        }

        return false;
    }
}