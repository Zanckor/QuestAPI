package dev.zanckor.mod.common.network.message.dialogoption;

import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.dialog.abstractdialog.AbstractDialogOption;
import dev.zanckor.api.filemanager.dialog.ServerDialog;
import dev.zanckor.example.common.enumregistry.enumdialog.EnumOptionType;
import dev.zanckor.api.filemanager.quest.register.TemplateRegistry;
import dev.zanckor.mod.common.util.GsonManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Supplier;

public class AddQuest {
    EnumOptionType optionType;
    int optionID;
    int globalID;

    public AddQuest(EnumOptionType optionType, int globalID, int optionID) {
        this.optionType = optionType;
        this.globalID = globalID;
        this.optionID = optionID;
    }

    public AddQuest(FriendlyByteBuf buffer) {
        optionType = buffer.readEnum(EnumOptionType.class);
        globalID = buffer.readInt();
        optionID = buffer.readInt();
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
        buffer.writeEnum(optionType);
        buffer.writeInt(globalID);
        buffer.writeInt(optionID);
    }


    public static void handler(AddQuest msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            String  dialogGlobalID = LocateHash.currentGlobalDialog.get(player);

            Path path = LocateHash.getDialogLocation(dialogGlobalID);
            File dialogFile = path.toFile();
            AbstractDialogOption dialogTemplate = TemplateRegistry.getDialogTemplate(msg.optionType);

            try {
                ServerDialog dialog = (ServerDialog) GsonManager.getJson(dialogFile, ServerDialog.class);

                dialogTemplate.handler(ctx.get().getSender(), dialog, msg.optionID, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}