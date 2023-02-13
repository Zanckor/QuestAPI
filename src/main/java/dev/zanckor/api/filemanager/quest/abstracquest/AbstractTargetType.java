package dev.zanckor.api.filemanager.quest.abstracquest;

import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public abstract class AbstractTargetType {

    /**
     * Class that returns as a human-readable text the quest target
     * @param resourceLocation  Resource location of the target wanted to translate. Example: entity.minecraft.cow
     */

    public abstract String handler(ResourceLocation resourceLocation);
}
