package dev.zanckor.api.enuminterface.enumquest;

import dev.zanckor.api.filemanager.quest.abstracquest.AbstractGoal;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractTargetType;
import dev.zanckor.example.common.enumregistry.EnumRegistry;

public interface IEnumQuestGoal {
    AbstractGoal getQuest();

    default void registerEnumGoal(Class enumClass) {
        EnumRegistry.registerQuestGoal(enumClass);
    }
}
