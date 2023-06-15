package dev.zanckor.mod.common.network.message.quest;

import dev.zanckor.mod.common.network.handler.ServerHandler;
import dev.zanckor.mod.common.util.MCUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Supplier;

public class QuestDataPacket {

    /**
     * Packet to execute a quest handler from client to server
     * <p>
     * Be careful using this from client side, may cause exploits
     */

    Enum quest;
    UUID entity;

    public QuestDataPacket(Enum quest, Entity entity) {
        this.quest = quest;
        this.entity = entity.getUUID();
    }

    public QuestDataPacket(FriendlyByteBuf buffer) {
        this.quest = buffer.readEnum(Enum.class);
        this.entity = buffer.readUUID();
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
        buffer.writeEnum(quest);
        buffer.writeUUID(entity);
    }


    public static void handler(QuestDataPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                ServerPlayer player = ctx.get().getSender();
                LivingEntity entity = (LivingEntity) MCUtil.getEntityByUUID((ServerLevel) player.level, msg.entity);

                ServerHandler.questHandler(msg.quest, player, entity);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
