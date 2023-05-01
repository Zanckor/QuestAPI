package dev.zanckor.mod.common.network.message.dialogoption;

import dev.zanckor.example.common.enumregistry.enumdialog.EnumDialogOption;
import dev.zanckor.mod.common.network.handler.ServerHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class DialogRequestPacket {
    EnumDialogOption optionType;
    int optionID;
    UUID npc;

    public DialogRequestPacket(EnumDialogOption optionType, int optionID, Entity npc) {
        this.optionType = optionType;
        this.optionID = optionID;
        this.npc = npc.getUUID();
    }

    public DialogRequestPacket(FriendlyByteBuf buffer) {
        optionType = buffer.readEnum(EnumDialogOption.class);
        optionID = buffer.readInt();
        npc = buffer.readUUID();
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
        buffer.writeEnum(optionType);
        buffer.writeInt(optionID);
        buffer.writeUUID(npc);
    }


    public static void handler(DialogRequestPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();

            ServerHandler.requestDialog(player, msg.optionID, msg.optionType, msg.npc);
        });

        ctx.get().setPacketHandled(true);
    }
}