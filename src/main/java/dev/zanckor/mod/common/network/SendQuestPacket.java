package dev.zanckor.mod.common.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;

public class SendQuestPacket {

    public static void NEAR(Player player, Object packet, double radius) {
        NetworkHandler.CHANNEL.send(
                PacketDistributor.NEAR.with(() ->
                        new PacketDistributor.TargetPoint(player.getX(), player.getY(), player.getZ(), radius, player.level.dimension())),
                packet);
    }

    public static void ALL(Object packet) {
        NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
    }


    public static void TO_CLIENT(Player player, Object packet) {
        NetworkHandler.CHANNEL.sendTo(packet, ((ServerPlayer) player).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void TO_SERVER(Object packet) {
        NetworkHandler.CHANNEL.sendToServer(packet);
    }


    public static void DIMENSION(Player player, Object packet) {
        NetworkHandler.CHANNEL.send(
                PacketDistributor.DIMENSION.with(() ->
                        player.level.dimension()),
                packet);
    }
}
