package dev.zanckor.mod.client.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public abstract class AbstractQuestLog extends Screen {
    protected AbstractQuestLog(Component component) {
        super(component);
    }

    public abstract Screen modifyScreen(List<String> id, List<String> title);
}
