package com.zanckor.example.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;

public class NPCRenderer extends ZombieRenderer {
    public NPCRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }
}
