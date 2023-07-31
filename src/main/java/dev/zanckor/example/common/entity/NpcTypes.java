package dev.zanckor.example.common.entity;

import dev.zanckor.example.common.entity.server.NPCEntity;
import dev.zanckor.mod.QuestApiMain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class NpcTypes {
    public static final DeferredRegister<EntityType<?>>
            ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, QuestApiMain.MOD_ID);


    public static final RegistryObject<EntityType<NPCEntity>> NPC_ENTITY = ENTITY_TYPES.register("quest_npc",
            () -> EntityType.Builder.of(NPCEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(QuestApiMain.MOD_ID, "quest_npc").toString()));


    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
