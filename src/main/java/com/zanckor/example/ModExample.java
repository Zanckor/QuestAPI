package com.zanckor.example;

import com.zanckor.api.dialog.enumdialog.EnumOptionType;
import com.zanckor.api.quest.enumquest.EnumQuestRequirement;
import com.zanckor.api.quest.enumquest.EnumQuestReward;
import com.zanckor.api.quest.enumquest.EnumQuestType;
import com.zanckor.api.quest.register.TemplateRegistry;
import com.zanckor.example.handler.dialog.AddQuestHandler;
import com.zanckor.example.handler.dialog.CloseDialogHandler;
import com.zanckor.example.handler.dialog.OpenDialogHandler;
import com.zanckor.example.handler.quest.*;
import com.zanckor.example.handler.requirement.XpRequirement;
import com.zanckor.example.handler.reward.ItemReward;

public class ModExample {

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
        TemplateRegistry.registerRequirement(EnumQuestRequirement.XP, new XpRequirement());
    }
}
