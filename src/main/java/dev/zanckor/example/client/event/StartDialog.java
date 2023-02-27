package dev.zanckor.example.client.event;

import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.dialog.ServerDialog;
import dev.zanckor.api.filemanager.dialog.abstractdialog.AbstractDialogRequirement;
import dev.zanckor.api.filemanager.quest.register.TemplateRegistry;
import dev.zanckor.example.common.enumregistry.enumdialog.EnumRequirementType;
import dev.zanckor.mod.common.util.GsonManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class StartDialog {

    /**
     * @param player         The player
     * @param globalDialogID Dialog ID of dialog file player will see. Example: collect_items_dialog.json
     * @throws IOException Exception fired when server cannot read json file
     */

    public static void loadDialog(Player player, String globalDialogID, Entity entity) throws IOException {
        Path path = LocateHash.getDialogLocation(globalDialogID);

        File dialogFile = path.toFile();
        LocateHash.currentGlobalDialog.put(player, dialogFile.getName().substring(0, dialogFile.getName().length() - 5));

        ServerDialog dialog = (ServerDialog) GsonManager.getJsonClass(dialogFile, ServerDialog.class);


        for (int dialog_id = dialog.getDialog().size() - 1; dialog_id >= 0; dialog_id--) {
            if (dialog.getDialog().get(dialog_id).getRequirements().getType() == null) continue;

            EnumRequirementType requirementType = EnumRequirementType.valueOf(dialog.getDialog().get(dialog_id).getRequirements().getType());
            AbstractDialogRequirement dialogRequirement = TemplateRegistry.getDialogRequirement(requirementType);

            if (dialogRequirement != null && dialogRequirement.handler(player, dialog, dialog_id, entity)) return;
        }
    }
}