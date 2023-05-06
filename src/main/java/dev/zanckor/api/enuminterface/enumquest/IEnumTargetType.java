package dev.zanckor.api.enuminterface.enumquest;

import dev.zanckor.api.filemanager.quest.abstracquest.AbstractGoal;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractTargetType;
import dev.zanckor.example.common.enumregistry.EnumRegistry;

public interface IEnumTargetType {
    AbstractTargetType getTargetType();

    default void registerTargetType(Class enumClass) {
        EnumRegistry.registerTargetType(enumClass);
    }
}