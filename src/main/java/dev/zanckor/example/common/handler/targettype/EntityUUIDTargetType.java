package dev.zanckor.example.common.handler.targettype;

import dev.zanckor.api.filemanager.quest.abstracquest.AbstractTargetType;
import dev.zanckor.mod.common.util.MCUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class EntityUUIDTargetType extends AbstractTargetType {

    @Override
    public String handler(ResourceLocation resourceLocation, Level level) {
        LivingEntity entity = (LivingEntity) MCUtil.getEntityByUUID(level, UUID.fromString(resourceLocation.getPath()));

        String translationKey = entity != null ? entity.getType().getDescriptionId() : "";

        return translationKey;
    }
}
