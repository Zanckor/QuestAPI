package com.zanckor.example.event;

import com.zanckor.api.dialog.register.DialogRegistry;
import com.zanckor.api.quest.register.LoadQuestFromResources;
import com.zanckor.mod.QuestApiMain;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RegisterQuest {

    @SubscribeEvent
    public static void serverFolderManager(ServerAboutToStartEvent e) {
        LoadQuestFromResources.registerQuest(QuestApiMain.MOD_ID);
        DialogRegistry.registerDialog(QuestApiMain.MOD_ID);
    }
}
