package dev.zanckor.example;

import dev.zanckor.api.filemanager.dialog.abstractdialog.AbstractDialogOption;
import dev.zanckor.api.filemanager.dialog.abstractdialog.AbstractDialogRequirement;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractQuest;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractQuestRequirement;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractReward;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractTargetType;
import dev.zanckor.api.filemanager.quest.register.QuestTemplateRegistry;
import dev.zanckor.api.screen.ScreenRegistry;
import dev.zanckor.example.common.entity.NpcTypes;
import dev.zanckor.example.common.enumregistry.enumdialog.EnumOptionType;
import dev.zanckor.example.common.enumregistry.enumdialog.EnumRequirementType;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestRequirement;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestReward;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestType;
import dev.zanckor.example.common.handler.dialogoption.DialogAddQuest;
import dev.zanckor.example.common.handler.dialogoption.DialogCloseDialog;
import dev.zanckor.example.common.handler.dialogoption.DialogOpenDialog;
import dev.zanckor.example.common.handler.dialogrequirement.DialogRequirement;
import dev.zanckor.example.common.handler.dialogrequirement.QuestRequirement;
import dev.zanckor.example.common.handler.questrequirement.XpRequirement;
import dev.zanckor.example.common.handler.questreward.CommandReward;
import dev.zanckor.example.common.handler.questreward.ItemReward;
import dev.zanckor.example.common.handler.questreward.QuestReward;
import dev.zanckor.example.common.handler.questtype.CollectHandler;
import dev.zanckor.example.common.handler.questtype.InteractEntityHandler;
import dev.zanckor.example.common.handler.questtype.KillHandler;
import dev.zanckor.example.common.handler.questtype.MoveToHandler;
import dev.zanckor.example.common.handler.targettype.EntityTargetType;
import dev.zanckor.example.common.handler.targettype.ItemTargetType;
import dev.zanckor.example.client.screen.dialog.DialogScreen;
import dev.zanckor.example.client.screen.hud.RenderQuestTracker;
import dev.zanckor.example.client.screen.questlog.QuestLog;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static dev.zanckor.mod.QuestApiMain.MOD_ID;

public class ModExample {
    public ModExample() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        NpcTypes.register(modEventBus);

        registerQuest();
        registerReward();
        registerRequirement();
        registerDialog();
        registerTarget();
    }


    /**
     * You can create your own EnumClass to add your templates:
     * <p>
     *
     * @QuestTemplate Needs to extend {@link AbstractQuest}
     * @DialogTemplate Needs to extend {@link AbstractDialogOption}
     * @Reward Needs to extend {@link AbstractReward}
     * @QuestRequirement Needs to extend {@link AbstractQuestRequirement}
     * @DialogRequirement Needs to extend {@link AbstractDialogRequirement}
     * @TargetType Needs to extend {@link AbstractTargetType}
     */

    public static void registerQuest() {
        QuestTemplateRegistry.registerQuest(EnumQuestType.KILL, new KillHandler());
        QuestTemplateRegistry.registerQuest(EnumQuestType.INTERACT_ENTITY, new InteractEntityHandler());
        QuestTemplateRegistry.registerQuest(EnumQuestType.MOVE_TO, new MoveToHandler());
        QuestTemplateRegistry.registerQuest(EnumQuestType.COLLECT, new CollectHandler());
    }

    public static void registerReward() {
        QuestTemplateRegistry.registerReward(EnumQuestReward.ITEM, new ItemReward());
        QuestTemplateRegistry.registerReward(EnumQuestReward.COMMAND, new CommandReward());
        QuestTemplateRegistry.registerReward(EnumQuestReward.QUEST, new QuestReward());
    }

    public static void registerRequirement() {
        QuestTemplateRegistry.registerQuestRequirement(EnumQuestRequirement.XP, new XpRequirement());

        QuestTemplateRegistry.registerDialogRequirement(EnumRequirementType.DIALOG, new DialogRequirement());
        QuestTemplateRegistry.registerDialogRequirement(EnumRequirementType.QUEST, new QuestRequirement());
    }

    public static void registerDialog() {
        QuestTemplateRegistry.registerDialog(EnumOptionType.OPEN_DIALOG, new DialogOpenDialog());
        QuestTemplateRegistry.registerDialog(EnumOptionType.CLOSE_DIALOG, new DialogCloseDialog());
        QuestTemplateRegistry.registerDialog(EnumOptionType.ADD_QUEST, new DialogAddQuest());

    }

    public static void registerTarget() {
        QuestTemplateRegistry.registerTargetType(EnumQuestType.COLLECT, new ItemTargetType());
        QuestTemplateRegistry.registerTargetType(EnumQuestType.KILL, new EntityTargetType());
        QuestTemplateRegistry.registerTargetType(EnumQuestType.INTERACT_ENTITY, new EntityTargetType());
    }


    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModExample {

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