package dev.zanckor.mod.client.screen.abstractscreen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class AbstractDialog extends Screen {
    protected AbstractDialog(Component component) {
        super(component);
    }

    public abstract Screen modifyScreen(int dialogID, String text, int optionSize, HashMap<Integer, List<Integer>> optionIntegers, HashMap<Integer, List<String>> optionStrings, UUID npcUUID);
}
