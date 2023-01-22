package com.zanckor.mod.network;

import com.zanckor.mod.QuestApiMain;
import com.zanckor.mod.client.screen.DialogScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.List;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, value = Dist.CLIENT)
public class ClientHandler {
    public static void toastQuestCompleted(String questName){
        SystemToast.add(Minecraft.getInstance().getToasts(),
                SystemToast.SystemToastIds.PERIODIC_NOTIFICATION,
                Component.literal("Quest Completed"),
                Component.literal(questName));
    }
    public static void displayDialog(int dialogID, String text, int optionSize, HashMap<Integer, List<Integer>> optionIntegers, HashMap<Integer, List<String>> optionStrings){
        Minecraft.getInstance().setScreen(new DialogScreen(dialogID, text,
                optionSize, optionIntegers, optionStrings));
    }

    public static void closeDialog(){
        Minecraft.getInstance().setScreen(null);
    }
}
