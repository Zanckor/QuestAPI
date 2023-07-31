package dev.zanckor.example.common.handler.dialogoption;

import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.dialog.abstractdialog.AbstractDialogOption;
import dev.zanckor.api.filemanager.dialog.codec.NPCConversation;
import dev.zanckor.api.filemanager.dialog.codec.NPCDialog;
import dev.zanckor.example.common.enumregistry.enumdialog.EnumDialogOption;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.dialogoption.DisplayDialog;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.io.IOException;

public class DialogOpenDialog extends AbstractDialogOption {

    /**
     * When player clicks on an option which type is "OPEN_DIALOG" will try to change current screen to the dialog specified on that option.
     *
     * @param player            The player
     * @param dialog            DialogTemplate class with all dialog data
     * @param option_id         DialogOption ID, Returns the object inside the List DialogOption. This is not a parameter inside the .json file
     * @throws IOException      Exception fired when server cannot read json file
     */

    @Override
    public void handler(Player player, NPCConversation dialog, int option_id, Entity entity) throws IOException {
        int currentDialog = LocateHash.currentDialog.get(player);
        NPCDialog.DialogOption option = dialog.getDialog().get(currentDialog).getOptions().get(option_id);

        if (option.getType().equals(EnumDialogOption.OPEN_DIALOG.toString())) {
            SendQuestPacket.TO_CLIENT(player, new DisplayDialog(dialog, dialog.getIdentifier(), option.getDialog(), player, entity));
        }
    }

    @Override
    public void handler(Player player, NPCConversation dialog, int option_id, String resourceLocation) throws IOException {
        int currentDialog = LocateHash.currentDialog.get(player);
        NPCDialog.DialogOption option = dialog.getDialog().get(currentDialog).getOptions().get(option_id);

        if (option.getType().equals(EnumDialogOption.OPEN_DIALOG.toString())) {
            SendQuestPacket.TO_CLIENT(player, new DisplayDialog(dialog, dialog.getIdentifier(), option.getDialog(), player, resourceLocation));
        }
    }

    @Override
    public void handler(Player player, NPCConversation dialog, int option_id, Item item) throws IOException {
        int currentDialog = LocateHash.currentDialog.get(player);
        NPCDialog.DialogOption option = dialog.getDialog().get(currentDialog).getOptions().get(option_id);

        if (option.getType().equals(EnumDialogOption.OPEN_DIALOG.toString())) {
            SendQuestPacket.TO_CLIENT(player, new DisplayDialog(dialog, dialog.getIdentifier(), option.getDialog(), player, item));
        }
    }
}
