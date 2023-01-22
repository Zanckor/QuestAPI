package com.zanckor.example.handler.dialog;

import com.zanckor.api.dialog.abstractdialog.AbstractDialog;
import com.zanckor.api.dialog.abstractdialog.DialogTemplate;
import com.zanckor.api.dialog.enumdialog.EnumOptionType;
import com.zanckor.example.event.QuestEvent;
import com.zanckor.mod.network.SendQuestPacket;
import com.zanckor.mod.network.message.dialog.DisplayDialog;
import net.minecraft.world.entity.player.Player;

import java.io.IOException;

public class OpenDialogHandler extends AbstractDialog {

    //TODO añadir parametro con la opcion que voy a usar

    @Override
    public void handler(Player player, DialogTemplate dialog, int optionID) throws IOException {
        int currentDialog = QuestEvent.currentDialog.get(player);
        DialogTemplate.DialogOption option = dialog.getDialog().get(currentDialog).getOptions().get(optionID);

        if (option.getType().equals(EnumOptionType.OPEN_DIALOG.toString())) {
            SendQuestPacket.TO_CLIENT(player, new DisplayDialog(dialog, option.getDialog(), player));
        }
    }
}
