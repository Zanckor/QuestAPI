package dev.zanckor.example.common.handler.targettype;

import dev.zanckor.api.filemanager.quest.abstracquest.AbstractTargetType;
import net.minecraft.resources.ResourceLocation;

public class EntityTargetType extends AbstractTargetType {

    @Override
    public String handler(ResourceLocation resourceLocation) {
        String translationKey = resourceLocation.getPath();

        return translationKey;
    }
}
