package dev.zanckor.example.common.handler.targettype;

import dev.zanckor.api.filemanager.quest.abstracquest.AbstractTargetType;
import net.minecraft.resources.ResourceLocation;

public class XPTargetType extends AbstractTargetType {

    @Override
    public String handler(ResourceLocation resourceLocation) {
        return "XP";
    }
}
