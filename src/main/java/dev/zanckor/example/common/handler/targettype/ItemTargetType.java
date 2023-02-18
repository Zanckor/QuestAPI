package dev.zanckor.example.common.handler.targettype;

import dev.zanckor.api.filemanager.quest.abstracquest.AbstractTargetType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemTargetType extends AbstractTargetType {
    @Override
    public String handler(ResourceLocation resourceLocation, Level level) {
        Item itemTarget = ForgeRegistries.ITEMS.getValue(resourceLocation);
        String translationKey = itemTarget.getDescriptionId(itemTarget.getDefaultInstance());

        return translationKey;
    }
}