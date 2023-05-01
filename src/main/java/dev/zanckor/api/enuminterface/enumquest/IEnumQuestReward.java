package dev.zanckor.api.enuminterface.enumquest;

import dev.zanckor.api.filemanager.quest.abstracquest.AbstractReward;
import dev.zanckor.example.common.enumregistry.EnumRegistry;

public interface IEnumQuestReward {
    AbstractReward getReward();

    default void registerEnumReward(Class enumClass) {
        EnumRegistry.registerQuestReward(enumClass);
    }
}
