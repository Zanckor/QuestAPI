package com.zanckor.mod.network.message.dialog;

import com.google.gson.Gson;
import com.zanckor.api.database.LocateHash;
import com.zanckor.api.dialog.abstractdialog.AbstractDialogOption;
import com.zanckor.api.dialog.abstractdialog.DialogTemplate;
import com.zanckor.api.dialog.enumdialog.EnumOptionType;
import com.zanckor.api.quest.register.TemplateRegistry;
import com.zanckor.mod.util.MCUtil;
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
            int dialogGlobalID = LocateHash.currentGlobalDialog.get(player);

            Gson gson = new Gson().newBuilder().setPrettyPrinting().create();
            Path path = DialogTemplate.getDialogLocation(dialogGlobalID);
            File dialogFile = path.toFile();
            AbstractDialogOption dialogTemplate = TemplateRegistry.getDialogTemplate(msg.optionType);

            try {
                DialogTemplate dialog = MCUtil.getJsonDialog(dialogFile, gson);

                dialogTemplate.handler(ctx.get().getSender(), dialog, msg.optionID);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}