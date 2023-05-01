package dev.zanckor.api.enuminterface.enumdialog;

import dev.zanckor.api.filemanager.dialog.abstractdialog.AbstractDialogRequirement;
import dev.zanckor.example.common.enumregistry.EnumRegistry;

public interface IEnumDialogReq {
    AbstractDialogRequirement getDialogRequirement();

    default void registerEnumDialogReq(Class enumClass) {
        EnumRegistry.registerDialogRequirement(enumClass);
    }
}
