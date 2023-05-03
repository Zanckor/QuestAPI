package dev.zanckor.mod.common.network.handler;

import dev.zanckor.api.filemanager.quest.codec.server.ServerQuest;
import dev.zanckor.api.filemanager.quest.codec.user.UserGoal;
import dev.zanckor.api.filemanager.quest.codec.user.UserQuest;
import dev.zanckor.api.screen.ScreenRegistry;
import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.client.screen.abstractscreen.AbstractDialog;
import dev.zanckor.mod.client.screen.questmaker.QuestMakerManager;
import dev.zanckor.mod.common.util.GsonManager;
import dev.zanckor.mod.common.util.MCUtilClient;
import dev.zanckor.mod.common.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.util.*;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, value = Dist.CLIENT)
public class ClientHandler {

    public static UserQuest userQuest;
    public static String questID;
    public static String questTitle;
    public static List<UserGoal> questGoals;
    public static boolean questHasTimeLimit;
    public static int questTimeLimit;
    public static List<String> activeQuestList;

    public static List<EntityType> availableEntityTypeForQuest = new ArrayList<>();
    public static Map<String, String> availableEntityTagForQuest = new HashMap<>();

    public static void toastQuestCompleted(String questName) {
        String title = I18n.get(questTitle);

        SystemToast.add(Minecraft.getInstance().getToasts(), SystemToast.SystemToastIds.PERIODIC_NOTIFICATION, Component.literal("Quest Completed"), Component.literal(title));

        MCUtilClient.playSound(SoundEvents.NOTE_BLOCK_PLING.get(), 1, 2);
    }

    public static void displayDialog(String dialogIdentifier, int dialogID, String text, int optionSize, HashMap<Integer, List<Integer>> optionIntegers, HashMap<Integer, List<String>> optionStrings, UUID npc) {
        AbstractDialog dialogScreen = ScreenRegistry.getDialogScreen(dialogIdentifier);

        Minecraft.getInstance().setScreen(dialogScreen.modifyScreen(dialogID, text, optionSize, optionIntegers, optionStrings, npc));
    }

    public static void closeDialog() {
        Minecraft.getInstance().setScreen(null);
    }

    public static void setQuestTracked(UserQuest userQuest) {
        ClientHandler.userQuest = userQuest;

        questTitle = userQuest.getTitle();
        questID = userQuest.getId();
        questHasTimeLimit = userQuest.hasTimeLimit();
        questTimeLimit = userQuest.getTimeLimitInSeconds();
        questGoals = userQuest.getQuestGoals();

        if (!Timer.existsTimer(Minecraft.getInstance().player.getUUID(), "TIMER_QUEST" + questID) && questHasTimeLimit) {
            Timer.updateCooldown(Minecraft.getInstance().player.getUUID(), "TIMER_QUEST" + questID, questTimeLimit);
        }
    }

    public static void updateQuestTracked(UserQuest userQuest) {
        if (!(userQuest.getId().equals(ClientHandler.userQuest.getId()))) return;

        ClientHandler.userQuest = userQuest;

        questTitle = userQuest.getTitle();
        questID = userQuest.getId();
        questHasTimeLimit = userQuest.hasTimeLimit();
        questTimeLimit = userQuest.getTimeLimitInSeconds();
        questGoals = userQuest.getQuestGoals();

        if (!Timer.existsTimer(Minecraft.getInstance().player.getUUID(), "TIMER_QUEST" + questID) && questHasTimeLimit) {
            Timer.updateCooldown(Minecraft.getInstance().player.getUUID(), "TIMER_QUEST" + questID, questTimeLimit);
        }
    }

    public static void removeQuest(String id) {
        Timer.clearTimer(Minecraft.getInstance().player.getUUID(), "TIMER_QUEST" + id);
    }


    public static void setActiveQuestList(List<String> activeQuestList) {
        ClientHandler.activeQuestList = activeQuestList;
    }

    public static void setAvailableServerQuestList(List<String> availableServerQuestList) {
        try {
            QuestMakerManager.availableQuests.clear();

            for (int i = 0; i < availableServerQuestList.size(); i++) {
                ServerQuest quest = (ServerQuest) GsonManager.getJsonClass(availableServerQuestList.get(i), ServerQuest.class);
                QuestMakerManager.availableQuests.add(quest);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void setAvailableEntityTypeForQuest(List<String> entityTypeForQuest, Map<String, String> entityTagMap) {
        for (String entityTypeString : entityTypeForQuest) {
            ResourceLocation entityResourceLocation = new ResourceLocation(entityTypeString.strip());

            EntityType entityType = ForgeRegistries.ENTITY_TYPES.getValue(entityResourceLocation);

            availableEntityTypeForQuest.add(entityType);
        }

        availableEntityTagForQuest = entityTagMap;
    }
}
