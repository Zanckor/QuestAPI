package dev.zanckor.example.server.event.questevent;

import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.common.config.server.GoalConfig;
import dev.zanckor.mod.common.network.handler.ServerHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

import static dev.zanckor.example.common.enumregistry.enumquest.EnumGoalType.LOCATE_STRUCTURE;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LocateStructureEvent {

    @SubscribeEvent
    public static void moveToQuest(TickEvent.PlayerTickEvent e) throws IOException {
        if (e.player == null || e.side.isClient() || e.player.getServer().getTickCount() % GoalConfig.LOCATE_STRUCTURE_COOLDOWN.get() != 0)
            return;

        ServerHandler.questHandler(LOCATE_STRUCTURE, (ServerPlayer) e.player, null);
    }
}
