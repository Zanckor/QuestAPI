package com.zanckor.example.event.npc;

import com.zanckor.example.entity.NpcTypes;
import com.zanckor.example.entity.server.NPCEntity;
import com.zanckor.mod.QuestApiMain;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AssignEntityStats {
    @SubscribeEvent
    public static void assignEntityAtts(EntityAttributeCreationEvent e) {
        e.put(NpcTypes.NPC_ENTITY.get(), NPCEntity.setAttributes());
    }
}
