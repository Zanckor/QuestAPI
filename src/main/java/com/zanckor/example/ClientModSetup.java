package com.zanckor.example;

import com.zanckor.example.entity.NpcTypes;
import com.zanckor.example.entity.client.NPCRenderer;
import com.zanckor.mod.QuestApiMain;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;


@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModSetup {

    @SubscribeEvent
    public static void registerOverlays(FMLClientSetupEvent e) {
        EntityRenderers.register(NpcTypes.NPC_ENTITY.get(), NPCRenderer::new);
    }
}
