package dev.zanckor.mod;

import com.mojang.blaze3d.platform.InputConstants;
import dev.zanckor.api.screen.ScreenRegistry;
import dev.zanckor.mod.client.screen.AbstractQuestTracked;
import dev.zanckor.mod.common.config.client.ScreenConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandlerRegister {
    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent e) {
        e.registerAboveAll("quest_tracker", (gui, poseStack, partialTick, width, height) -> {
            Player player = Minecraft.getInstance().player;

            if (player != null && !player.isDeadOrDying()) {
                AbstractQuestTracked abstractQuestTracked = ScreenRegistry.getQuestTrackedScreen(ScreenConfig.QUEST_TRACKED_SCREEN.get());
                abstractQuestTracked.renderQuestTracked(poseStack, width, height);
            }
        });
    }

    public static KeyMapping questMenu;

    public static KeyMapping registerKey(String name, int keycode) {
        final var key = new KeyMapping("key." + QuestApiMain.MOD_ID + "." + name, keycode, "key.categories.QuestApi");

        return key;
    }

    @SubscribeEvent
    public static void keyInit(RegisterKeyMappingsEvent e) {
        questMenu = registerKey("quest_menu", InputConstants.KEY_K);

        e.register(questMenu);
    }
}