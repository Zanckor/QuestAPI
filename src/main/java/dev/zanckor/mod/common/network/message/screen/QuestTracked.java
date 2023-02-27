package dev.zanckor.mod.common.network.message.screen;

import dev.zanckor.api.filemanager.quest.UserQuest;
import dev.zanckor.mod.common.network.ClientHandler;
import dev.zanckor.mod.common.util.GsonManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class QuestTracked {
    private String userQuest;


    public QuestTracked(UserQuest userQuest) {
        this.userQuest = GsonManager.gson().toJson(userQuest);
    }

    public QuestTracked(FriendlyByteBuf buffer) {
        userQuest = buffer.readUtf();
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
        buffer.writeUtf(userQuest);
    }


    public static void handler(QuestTracked msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                File file = File.createTempFile("userQuest", "json");
                Files.writeString(file.toPath(), msg.userQuest);
                UserQuest userQuest = (UserQuest) GsonManager.getJsonClass(file, UserQuest.class);

                if(userQuest != null) DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.questTracked(userQuest));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}

