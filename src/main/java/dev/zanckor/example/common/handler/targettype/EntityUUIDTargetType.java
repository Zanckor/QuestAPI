package dev.zanckor.example.common.handler.targettype;

import dev.zanckor.api.filemanager.quest.abstracquest.AbstractTargetType;
import dev.zanckor.mod.common.util.MCUtilClient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

public class EntityUUIDTargetType extends AbstractTargetType {

    @Override
    public String handler(ResourceLocation resourceLocation) {
        LivingEntity entity = (LivingEntity) MCUtilClient.getEntityByUUID(UUID.fromString(resourceLocation.getPath()));
        String translationKey = entity != null ? entity.getType().getDescriptionId() : "";

        return translationKey;
    }
}
