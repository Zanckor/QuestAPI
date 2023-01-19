package com.zanckor.example;

import com.zanckor.api.EnumQuestRequirement;
import com.zanckor.api.EnumQuestReward;
import com.zanckor.api.EnumQuestType;
import com.zanckor.api.questregister.register.TemplateRegistry;
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

        TemplateRegistry.registerReward(EnumQuestReward.ITEM, new ItemReward());
        TemplateRegistry.registerRequirement(EnumQuestRequirement.XP, new XpRequirement());
    }
}
