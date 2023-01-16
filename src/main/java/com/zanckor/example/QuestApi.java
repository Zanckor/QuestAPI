package com.zanckor.example;

import com.zanckor.api.EnumQuestType;
import com.zanckor.api.quest_register.QuestRegistry;
import com.zanckor.example.message_handler.*;

public class QuestApi {

    public static void initialize() {
        QuestRegistry.registerQuest(EnumQuestType.KILL, new KillHandler());
        QuestRegistry.registerQuest(EnumQuestType.INTERACT_ENTITY, new InteractEntityHandler());
        QuestRegistry.registerQuest(EnumQuestType.MOVE_TO, new MoveToHandler());
        QuestRegistry.registerQuest(EnumQuestType.PROTECT_ENTITY, new ProtectEntityHandler());
        QuestRegistry.registerQuest(EnumQuestType.RECOLLECT, new RecollectHandler());
    }
}
