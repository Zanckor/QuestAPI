package dev.zanckor.example;

import dev.zanckor.api.filemanager.dialog.abstractdialog.AbstractDialogOption;
import dev.zanckor.api.filemanager.dialog.abstractdialog.AbstractDialogRequirement;
import dev.zanckor.api.filemanager.dialog.register.LoadDialog;
import dev.zanckor.api.filemanager.npc.entity_type.register.LoadDialogList;
import dev.zanckor.api.filemanager.npc.entity_type_tag.register.LoadTagDialogList;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractGoal;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractQuestRequirement;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractReward;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractTargetType;
import dev.zanckor.api.filemanager.quest.register.LoadQuest;
import dev.zanckor.api.filemanager.quest.register.QuestTemplateRegistry;
import dev.zanckor.api.screen.ScreenRegistry;
import dev.zanckor.example.client.screen.dialog.DialogScreen;
import dev.zanckor.example.client.screen.hud.RenderQuestTracker;
import dev.zanckor.example.client.screen.questlog.QuestLog;
import dev.zanckor.example.common.entity.NpcTypes;
import dev.zanckor.example.common.enumregistry.enumdialog.EnumDialogOption;
import dev.zanckor.example.common.enumregistry.enumdialog.EnumDialogReq;
import dev.zanckor.example.common.enumregistry.enumquest.EnumGoalType;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestRequirement;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestReward;
import dev.zanckor.mod.QuestApiMain;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.IOException;
import java.util.Arrays;

import static dev.zanckor.mod.QuestApiMain.MOD_ID;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModExample {

    /**
     * You can create your own EnumClass to add your templates:
     * <p><p>
     * <p>
     * DialogOption Needs to extend {@link AbstractDialogOption} <p>
     * Goal Needs to extend {@link AbstractGoal} <p>
     * Reward Needs to extend {@link AbstractReward} <p>
     * QuestRequirement Needs to extend {@link AbstractQuestRequirement} <p>
     * DialogRequirement Needs to extend {@link AbstractDialogRequirement} <p>
     * TargetType Needs to extend {@link AbstractTargetType}
     */


    public ModExample() {
        QuestApiMain.LOGGER.debug("Creating Example code");

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        NpcTypes.register(modEventBus);

        Arrays.stream(EnumGoalType.values()).forEach(QuestTemplateRegistry::registerQuest);
        Arrays.stream(EnumQuestReward.values()).forEach(QuestTemplateRegistry::registerReward);
        Arrays.stream(EnumQuestRequirement.values()).forEach(QuestTemplateRegistry::registerQuestRequirement);
        Arrays.stream(EnumDialogReq.values()).forEach(QuestTemplateRegistry::registerDialogRequirement);
        Arrays.stream(EnumDialogOption.values()).forEach(QuestTemplateRegistry::registerDialogOption);
    }


    @SubscribeEvent
    public static void registerTemplates(ServerAboutToStartEvent e) throws IOException {
        QuestApiMain.LOGGER.debug("Register Template files");

        LoadQuest.registerQuest(e.getServer(), QuestApiMain.MOD_ID);
        LoadDialog.registerDialog(e.getServer(), QuestApiMain.MOD_ID);
        LoadDialogList.registerNPCDialogList(e.getServer(), QuestApiMain.MOD_ID);
        LoadTagDialogList.registerNPCTagDialogList(e.getServer(), QuestApiMain.MOD_ID);


        //Do not mind bout this, is background logic for data-packs.
        LoadQuest.registerDatapackQuest(e.getServer());
        LoadDialog.registerDatapackDialog(e.getServer());
        LoadDialogList.registerDatapackDialogList(e.getServer());
        LoadTagDialogList.registerDatapackTagDialogList(e.getServer());
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModExample {

        @SubscribeEvent
        public static void registerTargetTypeEnum(FMLClientSetupEvent e) {
            Arrays.stream(EnumGoalType.EnumTargetType.values()).forEach(QuestTemplateRegistry::registerTargetType);
        }


        /**
         * registerScreen adds specified classes to cache to load X or Y screen depending on your identifier and config file
         */

        @SubscribeEvent
        public static void registerScreen(FMLClientSetupEvent e) {
            ScreenRegistry.registerDialogScreen(MOD_ID, new DialogScreen(Component.literal("dialog_screen")));
            ScreenRegistry.registerQuestTrackedScreen(MOD_ID, new RenderQuestTracker());
            ScreenRegistry.registerQuestLogScreen(MOD_ID, new QuestLog(Component.literal("quest_log_screen")));
        }
    }
}