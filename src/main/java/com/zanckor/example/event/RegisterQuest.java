package com.zanckor.example.event;

import com.zanckor.api.questregister.QuestRegistry;
import com.zanckor.mod.QuestApiMain;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RegisterQuest {

    @SubscribeEvent
    public static void serverFolderManager(ServerAboutToStartEvent e) throws IOException {
        QuestRegistry.registerQuest(QuestApiMain.MOD_ID);
    }
}
