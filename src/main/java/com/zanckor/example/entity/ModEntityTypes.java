package com.zanckor.example.entity;

import com.zanckor.example.entity.server.NPCEntity;
import com.zanckor.mod.QuestApiMain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>>
            ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, QuestApiMain.MOD_ID);


    public static final RegistryObject<EntityType<NPCEntity>> NPC_ENTITY = ENTITY_TYPES.register("quest_npc",
            () -> EntityType.Builder.of(NPCEntity::new, MobCategory.MONSTER)
                    .sized(1f, 1f)
                    .canSpawnFarFromPlayer()
                    .setShouldReceiveVelocityUpdates(true)
                    .build(new ResourceLocation(QuestApiMain.MOD_ID, "quest_npc").toString()));


    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
