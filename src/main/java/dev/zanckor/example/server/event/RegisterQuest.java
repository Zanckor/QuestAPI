package dev.zanckor.example.server.event;

import dev.zanckor.api.filemanager.dialog.register.LoadDialog;
import dev.zanckor.api.filemanager.npc.entity_type.register.LoadDialogList;
import dev.zanckor.api.filemanager.npc.entity_type_tag.register.LoadTagDialogList;
import dev.zanckor.api.filemanager.quest.register.LoadQuest;
import dev.zanckor.mod.QuestApiMain;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RegisterQuest {

    @SubscribeEvent
    public static void serverFolderManager(ServerAboutToStartEvent e) throws IOException {
        LoadQuest.registerQuest(e.getServer(), QuestApiMain.MOD_ID);
        LoadDialog.registerDialog(e.getServer(), QuestApiMain.MOD_ID);
        LoadDialogList.registerNPCDialogList(e.getServer(), QuestApiMain.MOD_ID);
        LoadTagDialogList.registerNPCTagDialogList(e.getServer(), QuestApiMain.MOD_ID);

        LoadQuest.registerDatapackQuest(e.getServer());
        LoadDialog.registerDatapackDialog(e.getServer());
        LoadDialogList.registerDatapackDialogList(e.getServer());
        LoadTagDialogList.registerDatapackTagDialogList(e.getServer());
    }
}
