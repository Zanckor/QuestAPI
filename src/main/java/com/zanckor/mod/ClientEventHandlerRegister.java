package com.zanckor.mod;

import com.zanckor.mod.client.screen.RenderQuestTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
}