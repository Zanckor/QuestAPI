package com.zanckor.mod.network;

import com.zanckor.mod.QuestApiMain;
import com.zanckor.mod.client.screen.DialogScreen;
import com.zanckor.mod.client.screen.QuestListScreen;
import com.zanckor.mod.util.MCUtil;
import com.zanckor.mod.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, value = Dist.CLIENT)
public class ClientHandler {

    public static String trackedID;
    public static String trackedTitle;
    public static String trackedQuest_type;
    public static boolean trackedQuest_completed;
    public static List<String> trackedQuest_target = new ArrayList<>();
    public static List<Integer> trackedTarget_quantity, trackedTarget_current_quantity = new ArrayList<>();
    public static boolean trackedHasTimeLimit;
    public static int trackedTimeLimitInSeconds;


    public static void toastQuestCompleted(String questName) {
        SystemToast.add(Minecraft.getInstance().getToasts(),
                SystemToast.SystemToastIds.PERIODIC_NOTIFICATION,
                Component.literal("Quest Completed"),
                Component.literal(questName));

        MCUtil.playSound(SoundEvents.NOTE_BLOCK_PLING, 1, 2);
    }

    public static void displayDialog(int dialogID, String text, int optionSize, HashMap<Integer, List<Integer>> optionIntegers, HashMap<Integer, List<String>> optionStrings) {
        Minecraft.getInstance().setScreen(new DialogScreen(dialogID, text,
                optionSize, optionIntegers, optionStrings));
    }

    public static void closeDialog() {
        Minecraft.getInstance().setScreen(null);
    }


    public static void questTracked(String id, String title, String quest_type, boolean quest_completed, List<String> quest_target, List<Integer> target_quantity, List<Integer> target_current_quantity, boolean hasTimeLimit, int timeLimitInSeconds) {
        trackedID = id;
        trackedTitle = title;
        trackedQuest_type = quest_type;
        trackedQuest_completed = quest_completed;
        trackedQuest_target = quest_target;
        trackedTarget_quantity = target_quantity;
        trackedTarget_current_quantity = target_current_quantity;
        trackedHasTimeLimit = hasTimeLimit;


        if (!Timer.existsTimer(Minecraft.getInstance().player.getUUID(), "TIMER_QUEST" + id) && hasTimeLimit) {
            trackedTimeLimitInSeconds = timeLimitInSeconds;

            Timer.updateCooldown(Minecraft.getInstance().player.getUUID(), "TIMER_QUEST" + id, trackedTimeLimitInSeconds);
        }
    }

    public static void removeQuest(String id) {
        Timer.clearTimer(Minecraft.getInstance().player.getUUID(), "TIMER_QUEST" + id);
    }


    public static void displayQuestList(List<String> id, List<String> title) {
        Minecraft.getInstance().setScreen(new QuestListScreen(id, title));
    }
}
