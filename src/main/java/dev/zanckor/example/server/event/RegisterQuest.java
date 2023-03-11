package dev.zanckor.example.server.event;

import dev.zanckor.api.filemanager.dialog.register.LoadDialogFromResources;
import dev.zanckor.api.filemanager.quest.register.LoadQuestFromResources;
import dev.zanckor.mod.QuestApiMain;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RegisterQuest {

    @SubscribeEvent
    public static void serverFolderManager(ServerAboutToStartEvent e) throws IOException {
        LoadQuestFromResources.registerQuest(e.getServer(), QuestApiMain.MOD_ID);
        LoadDialogFromResources.registerDialog(e.getServer(), QuestApiMain.MOD_ID);

        LoadQuestFromResources.registerDatapackQuest(e.getServer());
        LoadDialogFromResources.registerDatapackDialog(e.getServer());
    }
}
