package dev.zanckor.example.event.questevent;

import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.network.SendQuestPacket;
import dev.zanckor.mod.network.message.quest.QuestDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static dev.zanckor.example.enumregistry.enumquest.EnumQuestType.KILL;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class KillEvent {
    @SubscribeEvent
    public static void killQuest(LivingDeathEvent e) {
        if (!(e.getSource().getEntity() instanceof Player)) return;

        SendQuestPacket.TO_SERVER(new QuestDataPacket(KILL));
    }
}