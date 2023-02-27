package dev.zanckor.mod.common.network;

import dev.zanckor.api.filemanager.quest.UserQuest;
import dev.zanckor.api.screen.ScreenRegistry;
import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.client.screen.dialog.AbstractDialog;
import dev.zanckor.mod.client.screen.questlog.QuestLog;
import dev.zanckor.mod.common.util.MCUtilClient;
import dev.zanckor.mod.common.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, value = Dist.CLIENT)
public class ClientHandler {

    public static UserQuest userQuest;
    public static String questID;
    public static String questTitle;
    public static List<UserQuest.QuestGoal> questGoals;
    public static boolean questHasTimeLimit;
    public static int questTimeLimit;


    public static void toastQuestCompleted(String questName) {
        SystemToast.add(Minecraft.getInstance().getToasts(),
                SystemToast.SystemToastIds.PERIODIC_NOTIFICATION,
                Component.literal("Quest Completed"),
                Component.literal(questName));

        MCUtilClient.playSound(SoundEvents.NOTE_BLOCK_PLING, 1, 2);
    }

    public static void displayDialog(String dialogIdentifier, int dialogID, String text, int optionSize, HashMap<Integer, List<Integer>> optionIntegers, HashMap<Integer, List<String>> optionStrings, UUID npc) {
        AbstractDialog screen = ScreenRegistry.getDialogScreen(dialogIdentifier);

        Minecraft.getInstance().setScreen(screen.modifyScreen(dialogID, text,
                optionSize, optionIntegers, optionStrings, npc));
    }

    public static void closeDialog() {
        Minecraft.getInstance().setScreen(null);
    }


    public static void questTracked(UserQuest userQuest) {
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


    public static void displayQuestList(List<String> id, List<String> title) {
        Minecraft.getInstance().setScreen(new QuestLog(id, title));
    }
}
