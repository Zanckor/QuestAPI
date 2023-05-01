package dev.zanckor.example.common.enumregistry.enumdialog;

import dev.zanckor.api.enuminterface.enumdialog.IEnumDialogReq;
import dev.zanckor.api.filemanager.dialog.abstractdialog.AbstractDialogRequirement;
import dev.zanckor.example.common.handler.dialogrequirement.DialogRequirement;
import dev.zanckor.example.common.handler.dialogrequirement.QuestRequirement;

public enum EnumDialogReq implements IEnumDialogReq {
    QUEST(new QuestRequirement()),
    DIALOG(new DialogRequirement());


    AbstractDialogRequirement dialogRequirement;
    EnumDialogReq(AbstractDialogRequirement abstractDialogRequirement) {
        dialogRequirement = abstractDialogRequirement;
        registerEnumDialogReq(this.getClass());
    }

    @Override
    public AbstractDialogRequirement getDialogRequirement() {
        return dialogRequirement;
    }
}
