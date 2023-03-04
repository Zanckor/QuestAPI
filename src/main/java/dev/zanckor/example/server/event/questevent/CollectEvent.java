package dev.zanckor.example.server.event.questevent;

import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.common.network.handler.ServerHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

import static dev.zanckor.example.common.enumregistry.enumquest.EnumQuestType.COLLECT;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CollectEvent {

    @SubscribeEvent
    public static void CollectPickUpQuest(PlayerEvent.ItemPickupEvent e) throws IOException {
        if(!(e.getEntity() instanceof ServerPlayer) || e.getEntity().level.isClientSide) return;


        sendQuestPacket((ServerPlayer) e.getEntity());
    }

    @SubscribeEvent
    public static void CollectCraftQuest(PlayerEvent.ItemCraftedEvent e) throws IOException {
        if(!(e.getEntity() instanceof ServerPlayer) || e.getEntity().level.isClientSide) return;

        sendQuestPacket((ServerPlayer) e.getEntity());
    }

    @SubscribeEvent
    public static void CollectCraftQuest(PlayerEvent.ItemSmeltedEvent e) throws IOException {
        if(!(e.getEntity() instanceof ServerPlayer) || e.getEntity().level.isClientSide) return;

        sendQuestPacket((ServerPlayer) e.getEntity());
    }

    public static void sendQuestPacket(ServerPlayer player) throws IOException {
        ServerHandler.questHandler(COLLECT, player, null);
    }
}
