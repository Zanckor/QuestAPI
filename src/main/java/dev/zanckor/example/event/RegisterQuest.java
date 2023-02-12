package dev.zanckor.example.event;

import dev.zanckor.api.dialog.register.DialogRegistry;
import dev.zanckor.api.quest.register.LoadQuestFromResources;
import dev.zanckor.mod.QuestApiMain;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RegisterQuest {

    @SubscribeEvent
    public static void serverFolderManager(ServerAboutToStartEvent e) {
        LoadQuestFromResources.registerQuest(e.getServer(), QuestApiMain.MOD_ID);
        DialogRegistry.registerDialog(e.getServer(), QuestApiMain.MOD_ID);
    }
}
