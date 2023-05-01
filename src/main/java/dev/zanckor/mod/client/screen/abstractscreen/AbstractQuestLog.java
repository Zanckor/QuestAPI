package dev.zanckor.mod.client.screen.abstractscreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.util.List;

public abstract class AbstractQuestLog extends Screen {

    protected AbstractQuestLog(Component component) {
        super(component);
    }

    public abstract Screen modifyScreen() throws IOException;
}
