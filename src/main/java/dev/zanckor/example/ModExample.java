package dev.zanckor.example;

import dev.zanckor.api.filemanager.dialog.abstractdialog.AbstractDialogOption;
import dev.zanckor.api.filemanager.dialog.abstractdialog.AbstractDialogRequirement;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractQuest;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractQuestRequirement;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractReward;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractTargetType;
import dev.zanckor.api.filemanager.quest.register.TemplateRegistry;
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
import dev.zanckor.example.common.handler.questtype.*;
import dev.zanckor.example.common.handler.targettype.EntityTargetType;
import dev.zanckor.example.common.handler.targettype.ItemTargetType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ModExample {
    public ModExample() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        NpcTypes.register(modEventBus);

        initialize();
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

    public static void initialize() {
        TemplateRegistry.registerQuest(EnumQuestType.KILL, new KillHandler());
        TemplateRegistry.registerQuest(EnumQuestType.INTERACT_ENTITY, new InteractEntityHandler());
        TemplateRegistry.registerQuest(EnumQuestType.MOVE_TO, new MoveToHandler());
        TemplateRegistry.registerQuest(EnumQuestType.PROTECT_ENTITY, new ProtectEntityHandler());
        TemplateRegistry.registerQuest(EnumQuestType.RECOLLECT, new RecollectHandler());

        TemplateRegistry.registerDialog(EnumOptionType.OPEN_DIALOG, new OpenDialogHandler());
        TemplateRegistry.registerDialog(EnumOptionType.CLOSE_DIALOG, new CloseDialogHandler());
        TemplateRegistry.registerDialog(EnumOptionType.ADD_QUEST, new AddQuestHandler());

        TemplateRegistry.registerReward(EnumQuestReward.ITEM, new ItemReward());
        TemplateRegistry.registerQuestRequirement(EnumQuestRequirement.XP, new XpRequirement());

        TemplateRegistry.registerDialogRequirement(EnumRequirementType.DIALOG, new DialogRequirementHandler());
        TemplateRegistry.registerDialogRequirement(EnumRequirementType.QUEST, new QuestRequirementHandler());

        TemplateRegistry.registerTargetType(EnumQuestType.RECOLLECT, new ItemTargetType());
        TemplateRegistry.registerTargetType(EnumQuestType.KILL, new EntityTargetType());
        TemplateRegistry.registerTargetType(EnumQuestType.INTERACT_ENTITY, new EntityTargetType());
    }
}