package com.zanckor.example.handler.dialogoption;

import com.zanckor.api.database.LocateHash;
import com.zanckor.api.dialog.abstractdialog.AbstractDialogOption;
import com.zanckor.api.dialog.abstractdialog.DialogTemplate;
import com.zanckor.api.dialog.enumdialog.EnumOptionType;
import com.zanckor.mod.network.SendQuestPacket;
import com.zanckor.mod.network.message.dialog.DisplayDialog;
import net.minecraft.world.entity.player.Player;

import java.io.IOException;

public class OpenDialogHandler extends AbstractDialogOption {

    @Override
    public void handler(Player player, DialogTemplate dialog, int optionID) throws IOException {
        int currentDialog = LocateHash.currentDialog.get(player);
        DialogTemplate.DialogOption option = dialog.getDialog().get(currentDialog).getOptions().get(optionID);

        if (option.getType().equals(EnumOptionType.OPEN_DIALOG.toString())) {
            SendQuestPacket.TO_CLIENT(player, new DisplayDialog(dialog, option.getDialog(), player));
        }
    }
}
