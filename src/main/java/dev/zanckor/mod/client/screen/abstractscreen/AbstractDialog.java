package dev.zanckor.mod.client.screen.abstractscreen;

import dev.zanckor.mod.common.network.message.dialogoption.DisplayDialog;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class AbstractDialog extends Screen {
    protected AbstractDialog(Component component) {
        super(component);
    }

    public abstract Screen modifyScreen(int dialogID, String text, int optionSize, HashMap<Integer, List<Integer>> optionIntegers, HashMap<Integer, List<String>> optionStrings, UUID npcUUID, String resourceLocation, Item item, DisplayDialog.NpcType npcType);
}
