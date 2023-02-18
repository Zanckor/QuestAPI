package dev.zanckor.example.common.handler.dialogoption;

import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.dialog.abstractdialog.AbstractDialogOption;
import dev.zanckor.api.filemanager.dialog.ServerDialog;
import dev.zanckor.example.common.enumregistry.enumdialog.EnumOptionType;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.dialogoption.DisplayDialog;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.io.IOException;

public class OpenDialogHandler extends AbstractDialogOption {

    /**
     * When player clicks on an option which type is "OPEN_DIALOG" will try to change current screen to the dialog specified on that option.
     *
     * @param player            The player
     * @param dialog            DialogTemplate class with all dialog data
     * @param option_id         DialogOption ID, Returns the object inside the List< DialogOption >. This is not a parameter inside the .json file
     * @throws IOException      Exception fired when server cannot read json file
     */

    @Override
    public void handler(Player player, ServerDialog dialog, int option_id, Entity npc) throws IOException {
        int currentDialog = LocateHash.currentDialog.get(player);

        ServerDialog.DialogOption option = dialog.getDialog().get(currentDialog).getOptions().get(option_id);


        if (option.getType().equals(EnumOptionType.OPEN_DIALOG.toString())) {
            SendQuestPacket.TO_CLIENT(player, new DisplayDialog(dialog, dialog.getIdentifier(), option.getDialog(), player, npc));
        }
    }
}
