package dev.zanckor.api.enuminterface.enumquest;

import dev.zanckor.api.filemanager.quest.abstracquest.AbstractQuestRequirement;
import dev.zanckor.example.common.enumregistry.EnumRegistry;

public interface IEnumQuestRequirement {
    AbstractQuestRequirement getRequirement();

    default void registerEnumQuestReq(Class enumClass) {
        EnumRegistry.registerQuestRequirement(enumClass);
    }
}
