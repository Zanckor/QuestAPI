package com.zanckor.example;

import com.zanckor.api.dialog.enumdialog.EnumOptionType;
import com.zanckor.api.dialog.enumdialog.EnumRequirementType;
import com.zanckor.api.quest.enumquest.EnumQuestRequirement;
import com.zanckor.api.quest.enumquest.EnumQuestReward;
import com.zanckor.api.quest.enumquest.EnumQuestType;
import com.zanckor.api.quest.register.TemplateRegistry;
import com.zanckor.example.entity.ModEntityTypes;
import com.zanckor.example.handler.dialogoption.AddQuestHandler;
import com.zanckor.example.handler.dialogoption.CloseDialogHandler;
import com.zanckor.example.handler.dialogoption.OpenDialogHandler;
import com.zanckor.example.handler.dialogrequirement.DialogRequirementHandler;
import com.zanckor.example.handler.dialogrequirement.QuestRequirementHandler;
import com.zanckor.example.handler.questtype.*;
import com.zanckor.example.handler.questrequirement.XpRequirement;
import com.zanckor.example.handler.questreward.ItemReward;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ModExample {
    public ModExample(){
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEntityTypes.register(modEventBus);

        initialize();
    }

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
    }
}
