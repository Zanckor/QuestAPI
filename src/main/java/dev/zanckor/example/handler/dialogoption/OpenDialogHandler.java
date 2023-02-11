package dev.zanckor.example.handler.dialogoption;

import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.dialog.abstractdialog.AbstractDialogOption;
import dev.zanckor.api.dialog.abstractdialog.DialogTemplate;
import dev.zanckor.example.enumregistry.enumdialog.EnumOptionType;
import dev.zanckor.mod.network.SendQuestPacket;
import dev.zanckor.mod.network.message.dialogoption.DisplayDialog;
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
    public void handler(Player player, DialogTemplate dialog, int option_id) throws IOException {
        int currentDialog = LocateHash.currentDialog.get(player);
        DialogTemplate.DialogOption option = dialog.getDialog().get(currentDialog).getOptions().get(option_id);

        if (option.getType().equals(EnumOptionType.OPEN_DIALOG.toString())) {
            SendQuestPacket.TO_CLIENT(player, new DisplayDialog(dialog, option.getDialog(), player));
        }
    }
}
