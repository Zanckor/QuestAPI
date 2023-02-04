package com.zanckor.example.handler.dialogrequirement;

import com.zanckor.api.database.LocateHash;
import com.zanckor.api.dialog.abstractdialog.AbstractDialogRequirement;
import com.zanckor.api.dialog.abstractdialog.DialogTemplate;
import com.zanckor.api.dialog.enumdialog.EnumRequirementStatusType;
import com.zanckor.api.dialog.enumdialog.EnumRequirementType;
import com.zanckor.mod.network.SendQuestPacket;
import com.zanckor.mod.network.message.dialogoption.DisplayDialog;
import com.zanckor.mod.util.MCUtil;
import net.minecraft.world.entity.player.Player;

import java.io.IOException;

public class DialogRequirementHandler extends AbstractDialogRequirement {


    @Override
    public boolean handler(Player player, DialogTemplate dialog, int dialog_id) throws IOException {
        String requirement = dialog.getDialog().get(dialog_id).getRequirements().getType();

        if (requirement.equals(EnumRequirementType.DIALOG.toString())) {
            EnumRequirementStatusType requirementStatus = EnumRequirementStatusType.valueOf(dialog.getDialog().get(dialog_id).getRequirements().getRequirement_status());
            int dialog_requirement = dialog.getDialog().get(dialog_id).getRequirements().getDialogId();

            switch (requirementStatus) {

                case READ -> {
                    if (MCUtil.isReadDialog(player, dialog_requirement)) {
                        displayDialog(player, dialog_id, dialog);
                        return true;
                    }
                }

                case NOT_READ -> {
                    if (!MCUtil.isReadDialog(player, dialog_id)) {
                        displayDialog(player, dialog_id, dialog);
                        return true;
                    }
                }
            }
        }

        return false;
    }


    private void displayDialog(Player player, int dialog_id, DialogTemplate dialog) throws IOException {
        LocateHash.currentDialog.put(player, dialog_id);
        SendQuestPacket.TO_CLIENT(player, new DisplayDialog(dialog, dialog_id, player));
    }
}
