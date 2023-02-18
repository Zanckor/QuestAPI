package dev.zanckor.example.common.handler.targettype;

import dev.zanckor.api.filemanager.quest.abstracquest.AbstractTargetType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class EntityTargetType extends AbstractTargetType {

    @Override
    public String handler(ResourceLocation resourceLocation, Level level) {
        String translationKey = resourceLocation.getPath();

        return translationKey;
    }
}
