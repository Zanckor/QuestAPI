package dev.zanckor.example.common.handler.dialogrequirement;

import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.dialog.codec.NPCConversation;
import dev.zanckor.api.filemanager.dialog.abstractdialog.AbstractDialogRequirement;
import dev.zanckor.api.filemanager.dialog.codec.NPCDialog;
import dev.zanckor.example.common.enumregistry.enumdialog.EnumDialogReqStatus;
import dev.zanckor.example.common.enumregistry.enumdialog.EnumDialogReq;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.dialogoption.DisplayDialog;
import dev.zanckor.mod.common.util.MCUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.io.IOException;

public class DialogRequirement extends AbstractDialogRequirement {

    /**
     * Dialog requirement status called to check if a player has read or not a dialog.
     *
     * @param player    The player
     * @param dialog    DialogTemplate class with all dialog data
     * @param option_id DialogOption ID, Returns the object inside the List DialogOption. This is not a parameter inside the .json file
     * @throws IOException Exception fired when server cannot read json file
     * @see EnumDialogReqStatus Requirement status
     */

    @Override
    public boolean handler(Player player, NPCConversation dialog, int option_id, Entity npc) throws IOException {
        NPCDialog.DialogRequirement requirement = dialog.getDialog().get(option_id).getServerRequirements();
        String requirementType = requirement.getType();
        if (!(requirementType.equals(EnumDialogReq.DIALOG.toString()))) return false;

        EnumDialogReqStatus requirementStatus = EnumDialogReqStatus.valueOf(requirement.getRequirement_status());
        int dialog_requirement = requirement.getDialogId();

        switch (requirementStatus) {
            case READ -> {
                if (MCUtil.isReadDialog(player, dialog_requirement)) {
                    displayDialog(player, option_id, dialog, npc);
                    return true;
                }
            }

            case NOT_READ -> {
                if (!MCUtil.isReadDialog(player, option_id)) {
                    displayDialog(player, option_id, dialog, npc);
                    return true;
                }
            }
        }

        return false;
    }


    private void displayDialog(Player player, int dialog_id, NPCConversation dialog, Entity npc) throws IOException {
        LocateHash.currentDialog.put(player, dialog_id);
        SendQuestPacket.TO_CLIENT(player, new DisplayDialog(dialog, dialog.getIdentifier(), dialog_id, player, npc));
    }
}
