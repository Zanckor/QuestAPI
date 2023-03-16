package dev.zanckor.api.filemanager.dialog.abstractdialog;

import dev.zanckor.api.filemanager.dialog.codec.ServerDialog;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.io.IOException;

public abstract class AbstractDialogOption {

    /**
     * Abstract class to call a registered dialog option
     * @param player            The player
     * @param dialog            DialogTemplate class with all dialog data
     * @param option_id         DialogOption ID, Returns the object inside the List< DialogOption >. This is not a parameter inside the .json file
     * @throws IOException      Exception fired when server cannot read json file
     */

    public abstract void handler(Player player, ServerDialog dialog, int option_id, Entity npc) throws IOException;
}
