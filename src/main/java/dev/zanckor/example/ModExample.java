package dev.zanckor.example;

import dev.zanckor.api.filemanager.dialog.abstractdialog.AbstractDialogOption;
import dev.zanckor.api.filemanager.dialog.abstractdialog.AbstractDialogRequirement;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractQuest;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractQuestRequirement;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractReward;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractTargetType;
import dev.zanckor.api.filemanager.quest.register.TemplateRegistry;
import dev.zanckor.api.screen.ScreenRegistry;
import dev.zanckor.example.common.entity.NpcTypes;
import dev.zanckor.example.common.enumregistry.enumdialog.EnumOptionType;
import dev.zanckor.example.common.enumregistry.enumdialog.EnumRequirementType;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestRequirement;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestReward;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestType;
import dev.zanckor.example.common.handler.dialogoption.AddQuestHandler;
import dev.zanckor.example.common.handler.dialogoption.CloseDialogHandler;
import dev.zanckor.example.common.handler.dialogoption.OpenDialogHandler;
import dev.zanckor.example.common.handler.dialogrequirement.DialogRequirementHandler;
import dev.zanckor.example.common.handler.dialogrequirement.QuestRequirementHandler;
import dev.zanckor.example.common.handler.questrequirement.XpRequirement;
import dev.zanckor.example.common.handler.questreward.ItemReward;
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
        TemplateRegistry.registerQuest(EnumQuestType.KILL, new KillHandler());
        TemplateRegistry.registerQuest(EnumQuestType.INTERACT_ENTITY, new InteractEntityHandler());
        TemplateRegistry.registerQuest(EnumQuestType.MOVE_TO, new MoveToHandler());
        TemplateRegistry.registerQuest(EnumQuestType.COLLECT, new CollectHandler());
    }

    public static void registerReward() {
        TemplateRegistry.registerReward(EnumQuestReward.ITEM, new ItemReward());
    }

    public static void registerRequirement() {
        TemplateRegistry.registerQuestRequirement(EnumQuestRequirement.XP, new XpRequirement());

        TemplateRegistry.registerDialogRequirement(EnumRequirementType.DIALOG, new DialogRequirementHandler());
        TemplateRegistry.registerDialogRequirement(EnumRequirementType.QUEST, new QuestRequirementHandler());
    }

    public static void registerDialog() {
        TemplateRegistry.registerDialog(EnumOptionType.OPEN_DIALOG, new OpenDialogHandler());
        TemplateRegistry.registerDialog(EnumOptionType.CLOSE_DIALOG, new CloseDialogHandler());
        TemplateRegistry.registerDialog(EnumOptionType.ADD_QUEST, new AddQuestHandler());

    }

    public static void registerTarget() {
        TemplateRegistry.registerTargetType(EnumQuestType.COLLECT, new ItemTargetType());
        TemplateRegistry.registerTargetType(EnumQuestType.KILL, new EntityTargetType());
        TemplateRegistry.registerTargetType(EnumQuestType.INTERACT_ENTITY, new EntityTargetType());
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