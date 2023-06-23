package dev.zanckor.mod.common.network.message.dialogoption;

import dev.zanckor.example.common.enumregistry.enumdialog.EnumDialogOption;
import dev.zanckor.mod.common.network.handler.ServerHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class DialogRequestPacket {
    EnumDialogOption optionType;
    int optionID;
    UUID entityUUID;
    String resourceLocation;
    Item item;
    DisplayDialog.NpcType npcType;

    public DialogRequestPacket(EnumDialogOption optionType, int optionID, Entity npc, String resourceLocation, Item item, DisplayDialog.NpcType npcType) {
        this.optionType = optionType;
        this.optionID = optionID;
        this.entityUUID = npc.getUUID();
        this.resourceLocation = resourceLocation;
        this.item = item;
        this.npcType = npcType;
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
        buffer.writeEnum(optionType);
        buffer.writeInt(optionID);

        encodeNpcType(buffer);
    }

    private void encodeNpcType(FriendlyByteBuf buf) {
        buf.writeEnum(npcType);

        switch (npcType) {
            case ITEM -> buf.writeItem(item.getDefaultInstance());
            case UUID -> buf.writeUUID(entityUUID);
            case RESOURCE_LOCATION -> buf.writeUtf(resourceLocation);
        }
    }


    public DialogRequestPacket(FriendlyByteBuf buffer) {
        optionType = buffer.readEnum(EnumDialogOption.class);
        optionID = buffer.readInt();
        decodeNpcType(buffer);
    }

    public void decodeNpcType(FriendlyByteBuf buf) {
        npcType = buf.readEnum(DisplayDialog.NpcType.class);

        switch (npcType) {
            case ITEM -> item = buf.readItem().getItem();
            case UUID -> entityUUID = buf.readUUID();
            case RESOURCE_LOCATION -> resourceLocation = buf.readUtf();
        }
    }

    public static void handler(DialogRequestPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();

            ServerHandler.requestDialog(player, msg.optionID, msg.optionType, msg.entityUUID, msg.resourceLocation, msg.item, msg.npcType);
        });

        ctx.get().setPacketHandled(true);
    }
}