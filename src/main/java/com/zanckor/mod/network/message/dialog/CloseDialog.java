package com.zanckor.mod.network.message.dialog;

import com.zanckor.api.dialog.abstractdialog.DialogTemplate;
import com.zanckor.mod.network.ClientHandler;
import com.zanckor.mod.util.MCUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class CloseDialog {

    public CloseDialog() {
    }

    public CloseDialog(FriendlyByteBuf buffer) {
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
    }


    public static void handler(CloseDialog msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.closeDialog());
        });

        ctx.get().setPacketHandled(true);
    }
}

