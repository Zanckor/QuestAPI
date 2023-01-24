package com.zanckor.example.handler.dialogrequirement;

import com.zanckor.api.database.LocateHash;
import com.zanckor.api.dialog.abstractdialog.AbstractDialogRequirement;
import com.zanckor.api.dialog.abstractdialog.DialogTemplate;
import com.zanckor.api.dialog.enumdialog.EnumRequirementStatusType;
import com.zanckor.api.dialog.enumdialog.EnumRequirementType;
import com.zanckor.mod.network.SendQuestPacket;
import com.zanckor.mod.network.message.dialog.DisplayDialog;
import com.zanckor.mod.util.MCUtil;
import net.minecraft.world.entity.player.Player;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.zanckor.mod.QuestApiMain.playerData;

public class DialogRequirementHandler extends AbstractDialogRequirement {


    @Override
    public boolean handler(Player player, DialogTemplate dialog, int dialog_id) throws IOException {
        String requirement = dialog.getDialog().get(dialog_id).getRequirements().getType();

        if (requirement.equals(EnumRequirementType.DIALOG.toString())) {
            EnumRequirementStatusType requirementStatus = EnumRequirementStatusType.valueOf(dialog.getDialog().get(dialog_id).getRequirements().getRequirement_status());

            switch (requirementStatus) {
                case READ -> {
                    if (MCUtil.isReadDialog(player, dialog_id)) {
                        displayDialog(player, dialog_id, dialog);
                        return true;
                    }

                    break;
                }

                case NOT_READ -> {
                    if (!MCUtil.isReadDialog(player, dialog_id)) {
                        displayDialog(player, dialog_id, dialog);
                        return true;
                    }

                    break;
                }
            }
        }

        return false;
    }


    private void displayDialog(Player player, int dialog_id, DialogTemplate dialog) throws IOException {
        LocateHash.currentDialog.put(player, dialog_id);
        LocateHash.currentGlobalDialog.put(player, dialog.getGlobal_id());

        System.out.println(MCUtil.isReadDialog(player, dialog_id));
        System.out.println(dialog.getDialog().get(dialog_id).getDialogTitle());

        SendQuestPacket.TO_CLIENT(player, new DisplayDialog(dialog, dialog_id, player));
    }
}
