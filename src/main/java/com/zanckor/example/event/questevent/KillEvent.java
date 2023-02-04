package com.zanckor.example.event.questevent;

import com.zanckor.mod.QuestApiMain;
import com.zanckor.mod.network.SendQuestPacket;
import com.zanckor.mod.network.message.quest.QuestDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.zanckor.api.quest.enumquest.EnumQuestType.KILL;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class KillEvent {
    @SubscribeEvent
    public static void killQuest(LivingDeathEvent e) {
        if (!(e.getSource().getEntity() instanceof Player)) return;

        SendQuestPacket.TO_SERVER(new QuestDataPacket(KILL));
    }
}