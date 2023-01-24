package com.zanckor.api.dialog.abstractdialog;

import net.minecraft.world.entity.player.Player;

import java.io.IOException;

public abstract class AbstractDialogRequirement {
    public abstract boolean handler(Player player, DialogTemplate dialog, int dialog_id) throws IOException;
}
