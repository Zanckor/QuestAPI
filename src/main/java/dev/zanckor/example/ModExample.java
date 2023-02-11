package dev.zanckor.example;

import dev.zanckor.api.dialog.abstractdialog.AbstractDialogOption;
import dev.zanckor.api.dialog.abstractdialog.AbstractDialogRequirement;
import dev.zanckor.api.quest.abstracquest.AbstractQuest;
import dev.zanckor.api.quest.abstracquest.AbstractQuestRequirement;
import dev.zanckor.api.quest.abstracquest.AbstractReward;
import dev.zanckor.api.quest.abstracquest.AbstractTargetType;
import dev.zanckor.api.quest.register.TemplateRegistry;
import dev.zanckor.example.entity.NpcTypes;
import dev.zanckor.example.enumregistry.enumdialog.EnumOptionType;
import dev.zanckor.example.enumregistry.enumdialog.EnumRequirementType;
import dev.zanckor.example.enumregistry.enumquest.EnumQuestRequirement;
import dev.zanckor.example.enumregistry.enumquest.EnumQuestReward;
import dev.zanckor.example.enumregistry.enumquest.EnumQuestType;
import dev.zanckor.example.handler.dialogoption.AddQuestHandler;
import dev.zanckor.example.handler.dialogoption.CloseDialogHandler;
import dev.zanckor.example.handler.dialogoption.OpenDialogHandler;
import dev.zanckor.example.handler.dialogrequirement.DialogRequirementHandler;
import dev.zanckor.example.handler.dialogrequirement.QuestRequirementHandler;
import dev.zanckor.example.handler.questrequirement.XpRequirement;
import dev.zanckor.example.handler.questreward.ItemReward;
import dev.zanckor.example.handler.questtype.*;
import dev.zanckor.example.handler.targettype.EntityTargetType;
import dev.zanckor.example.handler.targettype.ItemTargetType;
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
        TemplateRegistry.registerQuestTemplate(EnumQuestType.KILL, new KillHandler());
        TemplateRegistry.registerQuestTemplate(EnumQuestType.INTERACT_ENTITY, new InteractEntityHandler());
        TemplateRegistry.registerQuestTemplate(EnumQuestType.MOVE_TO, new MoveToHandler());
        TemplateRegistry.registerQuestTemplate(EnumQuestType.PROTECT_ENTITY, new ProtectEntityHandler());
        TemplateRegistry.registerQuestTemplate(EnumQuestType.RECOLLECT, new RecollectHandler());

        TemplateRegistry.registerDialogTemplate(EnumOptionType.OPEN_DIALOG, new OpenDialogHandler());
        TemplateRegistry.registerDialogTemplate(EnumOptionType.CLOSE_DIALOG, new CloseDialogHandler());
        TemplateRegistry.registerDialogTemplate(EnumOptionType.ADD_QUEST, new AddQuestHandler());

        TemplateRegistry.registerReward(EnumQuestReward.ITEM, new ItemReward());
        TemplateRegistry.registerQuestRequirement(EnumQuestRequirement.XP, new XpRequirement());

        TemplateRegistry.registerDialogRequirement(EnumRequirementType.DIALOG, new DialogRequirementHandler());
        TemplateRegistry.registerDialogRequirement(EnumRequirementType.QUEST, new QuestRequirementHandler());

        TemplateRegistry.registerTargetType(EnumQuestType.RECOLLECT, new ItemTargetType());
        TemplateRegistry.registerTargetType(EnumQuestType.KILL, new EntityTargetType());
        TemplateRegistry.registerTargetType(EnumQuestType.INTERACT_ENTITY, new EntityTargetType());
    }
}