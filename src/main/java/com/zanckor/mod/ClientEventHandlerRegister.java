package com.zanckor.mod;

import com.mojang.blaze3d.platform.InputConstants;
import com.zanckor.mod.client.screen.RenderQuestTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jline.keymap.KeyMap;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandlerRegister {
    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent e) {
        e.registerAboveAll("quest_tracker", (gui, poseStack, partialTick, width, height) -> {
            Player player = Minecraft.getInstance().player;

            if (player != null && !player.isDeadOrDying()) {
                RenderQuestTracker.renderQuestTracker(poseStack, width, height, 0);
            }
        });
    }

    public static KeyMapping questMenu;

    public static KeyMapping registerKey(String name, int keycode){
        final var key = new KeyMapping("key." + QuestApiMain.MOD_ID + "." + name, keycode, "key.categories.QuestApi");

        return key;
    }

    @SubscribeEvent
    public static void keyInit(RegisterKeyMappingsEvent e){
        questMenu = registerKey("quest_menu", InputConstants.KEY_K);

        e.register(questMenu);
    }
}