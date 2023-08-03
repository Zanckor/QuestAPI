package dev.zanckor.mod.client.screen.abstractscreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.io.IOException;

public abstract class AbstractQuestLog extends Screen {

    protected AbstractQuestLog(Component component) {
        super(component);
    }

    public abstract Screen modifyScreen() throws IOException;
}
