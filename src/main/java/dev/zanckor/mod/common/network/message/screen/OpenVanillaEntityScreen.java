package dev.zanckor.mod.common.network.message.screen;

import dev.zanckor.api.filemanager.quest.codec.UserQuest;
import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.common.network.handler.ClientHandler;
import dev.zanckor.mod.common.util.GsonManager;
import dev.zanckor.mod.common.util.MCUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class OpenVanillaEntityScreen {

    UUID entityUUID;


    public OpenVanillaEntityScreen(UUID entityUUID) {
        this.entityUUID = entityUUID;
    }

    public OpenVanillaEntityScreen(FriendlyByteBuf buffer) {
        entityUUID = buffer.readUUID();
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
        buffer.writeUUID(entityUUID);
    }


    public static void handler(OpenVanillaEntityScreen msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            Entity entity = MCUtil.getEntityByUUID(ctx.get().getSender().getLevel(), msg.entityUUID);

            player.setShiftKeyDown(true);
            player.interactOn(entity, InteractionHand.MAIN_HAND);
        });

        ctx.get().setPacketHandled(true);
    }
}