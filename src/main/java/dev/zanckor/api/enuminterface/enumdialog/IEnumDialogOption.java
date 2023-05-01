package dev.zanckor.api.enuminterface.enumdialog;

import dev.zanckor.api.filemanager.dialog.abstractdialog.AbstractDialogOption;
import dev.zanckor.example.common.enumregistry.EnumRegistry;

public interface IEnumDialogOption {
    AbstractDialogOption getDialogOption();

    default void registerEnumDialogOption(Class enumClass) {
        EnumRegistry.registerDialogOption(enumClass);
    }
}
