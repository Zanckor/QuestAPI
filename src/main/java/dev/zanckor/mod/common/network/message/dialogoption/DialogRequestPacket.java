package dev.zanckor.mod.common.network.message.dialogoption;

import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.dialog.ServerDialog;
import dev.zanckor.api.filemanager.dialog.abstractdialog.AbstractDialogOption;
import dev.zanckor.api.filemanager.quest.register.TemplateRegistry;
import dev.zanckor.example.common.enumregistry.enumdialog.EnumOptionType;
import dev.zanckor.mod.common.util.GsonManager;
import dev.zanckor.mod.common.util.MCUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Supplier;

public class DialogRequestPacket {
    EnumOptionType optionType;
    int optionID;
    UUID npc;

    public DialogRequestPacket(EnumOptionType optionType, int optionID, Entity npc) {
        this.optionType = optionType;
        this.optionID = optionID;
        this.npc = npc.getUUID();
    }

    public DialogRequestPacket(FriendlyByteBuf buffer) {
        optionType = buffer.readEnum(EnumOptionType.class);
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
            Player player = ctx.get().getSender();
            String globalDialogID = LocateHash.currentGlobalDialog.get(player);

            Path path = LocateHash.getDialogLocation(globalDialogID);
            File dialogFile = path.toFile();
            AbstractDialogOption dialogTemplate = TemplateRegistry.getDialogTemplate(msg.optionType);

            try {
                ServerDialog dialog = (ServerDialog) GsonManager.getJson(dialogFile, ServerDialog.class);
                dialogTemplate.handler(ctx.get().getSender(), dialog, msg.optionID, MCUtil.getEntityByUUID(player.level, msg.npc));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}