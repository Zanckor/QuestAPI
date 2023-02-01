package com.zanckor.example.handler.targettype;

import com.zanckor.api.quest.abstracquest.AbstractTargetType;
import net.minecraft.resources.ResourceLocation;

public class EntityTargetType extends AbstractTargetType {
    @Override
    public String handler(ResourceLocation resourceLocation) {
        String translationKey = resourceLocation.getPath();

        return translationKey;
    }
}
