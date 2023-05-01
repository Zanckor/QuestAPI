package dev.zanckor.mod.common.network.message.dialogoption;

import dev.zanckor.example.common.enumregistry.enumdialog.EnumDialogOption;
import dev.zanckor.mod.common.network.handler.ServerHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AddQuest {

    /**
     * Add question
     */

    EnumDialogOption optionType;
    int optionID;

    public AddQuest(EnumDialogOption optionType, int optionID) {
        this.optionType = optionType;
        this.optionID = optionID;
    }

    public AddQuest(FriendlyByteBuf buffer) {
        optionType = buffer.readEnum(EnumDialogOption.class);
        optionID = buffer.readInt();
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
        buffer.writeEnum(optionType);
        buffer.writeInt(optionID);
    }


    public static void handler(AddQuest msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();

            ServerHandler.addQuest(player, msg.optionType, msg.optionID);
        });

        ctx.get().setPacketHandled(true);
    }
}