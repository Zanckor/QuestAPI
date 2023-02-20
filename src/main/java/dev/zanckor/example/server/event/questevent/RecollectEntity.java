package dev.zanckor.example.server.event.questevent;

import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.common.network.ServerHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

import static dev.zanckor.example.common.enumregistry.enumquest.EnumQuestType.RECOLLECT;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RecollectEntity {

    @SubscribeEvent
    public static void recollectPickUpQuest(PlayerEvent.ItemPickupEvent e) throws IOException {
        if(!(e.getEntity() instanceof ServerPlayer) || e.getEntity().level.isClientSide) return;


        sendQuestPacket((ServerPlayer) e.getEntity());
    }

    @SubscribeEvent
    public static void recollectCraftQuest(PlayerEvent.ItemCraftedEvent e) throws IOException {
        if(!(e.getEntity() instanceof ServerPlayer) || e.getEntity().level.isClientSide) return;

        sendQuestPacket((ServerPlayer) e.getEntity());
    }

    @SubscribeEvent
    public static void recollectCraftQuest(PlayerEvent.ItemSmeltedEvent e) throws IOException {
        if(!(e.getEntity() instanceof ServerPlayer) || e.getEntity().level.isClientSide) return;

        sendQuestPacket((ServerPlayer) e.getEntity());
    }

    public static void sendQuestPacket(ServerPlayer player) throws IOException {
        ServerHandler.questHandler(RECOLLECT, player, null);
    }
}
