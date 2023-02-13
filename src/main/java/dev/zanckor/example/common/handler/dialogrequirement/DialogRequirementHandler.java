package dev.zanckor.example.common.handler.dialogrequirement;

import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.dialog.abstractdialog.AbstractDialogRequirement;
import dev.zanckor.api.filemanager.dialog.ServerDialog;
import dev.zanckor.example.common.enumregistry.enumdialog.EnumRequirementStatusType;
import dev.zanckor.example.common.enumregistry.enumdialog.EnumRequirementType;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.dialogoption.DisplayDialog;
import dev.zanckor.mod.common.util.MCUtil;
import net.minecraft.world.entity.player.Player;

import java.io.IOException;

public class DialogRequirementHandler extends AbstractDialogRequirement {

    /**
     * Dialog requirement status called to check if a player has read or not a dialog.
     *
     * @param player            The player
     * @param dialog            DialogTemplate class with all dialog data
     * @param option_id         DialogOption ID, Returns the object inside the List< DialogOption >. This is not a parameter inside the .json file
     * @throws IOException      Exception fired when server cannot read json file
     * @see EnumRequirementStatusType Requirement status
     */

    @Override
    public boolean handler(Player player, ServerDialog dialog, int option_id) throws IOException {
        String requirement = dialog.getDialog().get(option_id).getRequirements().getType();

        if (requirement.equals(EnumRequirementType.DIALOG.toString())) {
            EnumRequirementStatusType requirementStatus = EnumRequirementStatusType.valueOf(dialog.getDialog().get(option_id).getRequirements().getRequirement_status());
            int dialog_requirement = dialog.getDialog().get(option_id).getRequirements().getDialogId();

            switch (requirementStatus) {

                case READ -> {
                    if (MCUtil.isReadDialog(player, dialog_requirement)) {
                        displayDialog(player, option_id, dialog);
                        return true;
                    }
                }

                case NOT_READ -> {
                    if (!MCUtil.isReadDialog(player, option_id)) {
                        displayDialog(player, option_id, dialog);
                        return true;
                    }
                }
            }
        }

        return false;
    }


    private void displayDialog(Player player, int dialog_id, ServerDialog dialog) throws IOException {
        LocateHash.currentDialog.put(player, dialog_id);
        SendQuestPacket.TO_CLIENT(player, new DisplayDialog(dialog, dialog_id, player));
    }
}
