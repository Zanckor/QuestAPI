package dev.zanckor.example.common.handler.dialogrequirement;

import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.dialog.codec.NPCConversation;
import dev.zanckor.api.filemanager.dialog.abstractdialog.AbstractDialogRequirement;
import dev.zanckor.api.filemanager.dialog.codec.NPCDialog;
import dev.zanckor.api.filemanager.quest.codec.user.UserQuest;
import dev.zanckor.example.common.enumregistry.enumdialog.EnumDialogReqStatus;
import dev.zanckor.example.common.enumregistry.enumdialog.EnumDialogReq;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.dialogoption.DisplayDialog;
import dev.zanckor.mod.common.util.GsonManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class QuestRequirement extends AbstractDialogRequirement {

    /**
     * Quest requirement status called to check if a player has a quest, is completed, or never obtained it before.
     *
     * @param player    The player
     * @param dialog    DialogTemplate class with all dialog data
     * @param option_id DialogOption ID, Returns the object inside the List DialogOption. This is not a parameter inside the .json file
     * @throws IOException Exception fired when server cannot read json file
     * IMPORTANT If quest is removed it will be automatically set as "NOT_OBTAINED"
     * @see EnumDialogReqStatus Requirement status
     */

    @Override
    public boolean handler(Player player, NPCConversation dialog, int option_id, Entity npc) throws IOException {
        if (player.level().isClientSide) return false;
        NPCDialog.DialogRequirement requirement = dialog.getDialog().get(option_id).getServerRequirements();
        String requirementType = requirement.getType();
        if (!(requirementType.equals(EnumDialogReq.QUEST.toString()))) return false;

        EnumDialogReqStatus requirementStatus = EnumDialogReqStatus.valueOf(requirement.getRequirement_status());
        Path questPath = LocateHash.getQuestByID(requirement.getQuestId());
        File questFile;
        UserQuest playerQuest;

        if (questPath == null) {
            switch (requirementStatus) {
                case NOT_OBTAINED -> {
                    displayDialog(player, option_id, dialog, npc);

                    return true;
                }
            }
        } else {
            questFile = questPath.toFile();
            playerQuest = questFile.exists() ? (UserQuest) GsonManager.getJsonClass(questFile, UserQuest.class) : null;

            switch (requirementStatus) {
                case IN_PROGRESS -> {
                    if (questFile.exists() && !playerQuest.isCompleted()) {
                        displayDialog(player, option_id, dialog, npc);

                        return true;
                    }
                }


                case COMPLETED -> {
                    if (questFile.exists() && playerQuest.isCompleted()) {
                        displayDialog(player, option_id, dialog, npc);

                        return true;
                    }
                }

                case NOT_OBTAINED -> {
                    if (!questFile.exists()) {
                        displayDialog(player, option_id, dialog, npc);

                        return true;
                    }
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
