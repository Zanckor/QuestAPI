package dev.zanckor.example.common.handler.dialogrequirement;

import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.dialog.ServerDialog;
import dev.zanckor.api.filemanager.dialog.abstractdialog.AbstractDialogRequirement;
import dev.zanckor.api.filemanager.quest.UserQuest;
import dev.zanckor.example.common.enumregistry.enumdialog.EnumRequirementStatusType;
import dev.zanckor.example.common.enumregistry.enumdialog.EnumRequirementType;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.dialogoption.DisplayDialog;
import dev.zanckor.mod.common.util.GsonManager;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class QuestRequirementHandler extends AbstractDialogRequirement {

    /**
     * Quest requirement status called to check if a player has a quest, is completed, or never obtained it before.
     *
     * @param player    The player
     * @param dialog    DialogTemplate class with all dialog data
     * @param option_id DialogOption ID, Returns the object inside the List< DialogOption >. This is not a parameter inside the .json file
     * @throws IOException Exception fired when server cannot read json file
     * @IMPORTANT If quest is removed it will be automatically set as "NOT_OBTAINED"
     * @see EnumRequirementStatusType Requirement status
     */

    @Override
    public boolean handler(Player player, ServerDialog dialog, int option_id) throws IOException {
        if (player.level.isClientSide) return false;

        EnumRequirementStatusType requirementStatus = EnumRequirementStatusType.valueOf(dialog.getDialog().get(option_id).getRequirements().getRequirement_status());
        String requirement = dialog.getDialog().get(option_id).getRequirements().getType();
        Path questPath = LocateHash.getQuestByID(dialog.getDialog().get(option_id).getRequirements().getQuestId());

        File questFile;
        UserQuest playerQuest = null;

        if (requirement.equals(EnumRequirementType.QUEST.toString())) {

            if (questPath != null) {
                questFile = questPath.toFile();

                if (questFile.exists()) {
                    playerQuest = (UserQuest) GsonManager.getJson(questFile, UserQuest.class);
                }


                switch (requirementStatus) {
                    case IN_PROGRESS -> {
                        if (questFile.exists() && !playerQuest.isCompleted()) {
                            displayDialog(player, option_id, dialog);

                            return true;
                        }
                    }


                    case COMPLETED -> {
                        if (questFile.exists() && playerQuest.isCompleted()) {
                            displayDialog(player, option_id, dialog);

                            return true;
                        }
                    }

                    case NOT_OBTAINED -> {
                        if (!questFile.exists()) {
                            displayDialog(player, option_id, dialog);

                            return true;
                        }
                    }
                }
            } else {
                switch (requirementStatus) {
                    case NOT_OBTAINED -> {
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
