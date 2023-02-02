package com.zanckor.example.handler.dialogrequirement;

import com.zanckor.api.database.LocateHash;
import com.zanckor.api.dialog.abstractdialog.AbstractDialogRequirement;
import com.zanckor.api.dialog.abstractdialog.DialogTemplate;
import com.zanckor.api.dialog.enumdialog.EnumRequirementStatusType;
import com.zanckor.api.dialog.enumdialog.EnumRequirementType;
import com.zanckor.api.quest.ClientQuestBase;
import com.zanckor.mod.network.SendQuestPacket;
import com.zanckor.mod.network.message.dialogoption.DisplayDialog;
import com.zanckor.mod.util.MCUtil;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class QuestRequirementHandler extends AbstractDialogRequirement {


    @Override
    public boolean handler(Player player, DialogTemplate dialog, int dialog_id) throws IOException {
        if (player.level.isClientSide) return false;

        EnumRequirementStatusType requirementStatus = EnumRequirementStatusType.valueOf(dialog.getDialog().get(dialog_id).getRequirements().getRequirement_status());
        String requirement = dialog.getDialog().get(dialog_id).getRequirements().getType();
        Path questPath = LocateHash.getQuestByID(dialog.getDialog().get(dialog_id).getRequirements().getQuestId());

        File questFile;
        ClientQuestBase playerQuest = null;

        if (requirement.equals(EnumRequirementType.QUEST.toString())) {

            if (questPath != null) {
                questFile = questPath.toFile();

                if (questFile.exists()) {
                    playerQuest = MCUtil.getJsonClientQuest(questFile);
                }


                switch (requirementStatus) {
                    case IN_PROGRESS -> {
                        if (questFile.exists() && !playerQuest.isCompleted()) {
                            displayDialog(player, dialog_id, dialog);

                            return true;
                        }
                    }


                    case COMPLETED -> {
                        if (questFile.exists() && playerQuest.isCompleted()) {
                            displayDialog(player, dialog_id, dialog);

                            return true;
                        }
                    }

                    case NOT_OBTAINED -> {
                        if (!questFile.exists()) {
                            displayDialog(player, dialog_id, dialog);

                            return true;
                        }
                    }
                }
            } else {
                switch (requirementStatus) {
                    case NOT_OBTAINED -> {
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
        LocateHash.currentGlobalDialog.put(player, dialog.getGlobal_id());

        SendQuestPacket.TO_CLIENT(player, new DisplayDialog(dialog, dialog_id, player));
    }
}
