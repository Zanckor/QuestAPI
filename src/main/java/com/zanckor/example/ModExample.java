package com.zanckor.example;

import com.zanckor.api.EnumQuestType;
import com.zanckor.api.questregister.TemplateRegistry;
import com.zanckor.example.messagehandler.*;

public class ModExample {

    public static void initialize() {
        TemplateRegistry.registerTemplate(EnumQuestType.KILL, new KillHandler());
        TemplateRegistry.registerTemplate(EnumQuestType.INTERACT_ENTITY, new InteractEntityHandler());
        TemplateRegistry.registerTemplate(EnumQuestType.MOVE_TO, new MoveToHandler());
        TemplateRegistry.registerTemplate(EnumQuestType.PROTECT_ENTITY, new ProtectEntityHandler());
        TemplateRegistry.registerTemplate(EnumQuestType.RECOLLECT, new RecollectHandler());
    }
}
