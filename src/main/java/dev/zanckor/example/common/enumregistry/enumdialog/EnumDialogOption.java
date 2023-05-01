package dev.zanckor.example.common.enumregistry.enumdialog;

import dev.zanckor.api.enuminterface.enumdialog.IEnumDialogOption;
import dev.zanckor.api.filemanager.dialog.abstractdialog.AbstractDialogOption;
import dev.zanckor.example.common.handler.dialogoption.DialogAddQuest;
import dev.zanckor.example.common.handler.dialogoption.DialogCloseDialog;
import dev.zanckor.example.common.handler.dialogoption.DialogOpenDialog;

public enum EnumDialogOption implements IEnumDialogOption {
    OPEN_DIALOG(new DialogOpenDialog()),
    CLOSE_DIALOG(new DialogCloseDialog()),
    ADD_QUEST(new DialogAddQuest());

    AbstractDialogOption dialogOption;

    EnumDialogOption(AbstractDialogOption abstractDialogOption) {
        dialogOption = abstractDialogOption;
        registerEnumDialogOption(this.getClass());
    }

    @Override
    public AbstractDialogOption getDialogOption() {
        return dialogOption;
    }
}
