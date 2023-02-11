package dev.zanckor.api.dialog.abstractdialog;

import net.minecraft.world.entity.player.Player;

import java.io.IOException;

public abstract class AbstractDialogRequirement {
    /**
     * Abstract class to call a registered dialog requirement so on try to display a dialog first check the requirement for determinate option_id
     * @param player            The player
     * @param player            The player
     * @param dialog            DialogTemplate class with all dialog data
     * @param option_id         DialogOption ID, Returns the object inside the List< DialogOption >. This is not a parameter inside the .json file
     * @throws IOException      Exception fired when server cannot read json file
     */

    public abstract boolean handler(Player player, DialogTemplate dialog, int option_id) throws IOException;
}
