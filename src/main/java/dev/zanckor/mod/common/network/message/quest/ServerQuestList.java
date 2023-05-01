package dev.zanckor.mod.common.network.message.quest;

import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.common.network.handler.ClientHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ServerQuestList {
    List<String> questFileList = new ArrayList<>();
    int listSize;


    public ServerQuestList() {
        try {
            File[] serverQuests = QuestApiMain.serverQuests.toFile().listFiles();

            for (File file : serverQuests) {
                questFileList.add(Files.readString(file.toPath()));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ServerQuestList(FriendlyByteBuf buffer) {
        listSize = buffer.readInt();

        for (int i = 0; i < listSize; i++) {
            questFileList.add(buffer.readUtf());
        }
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
        buffer.writeInt(questFileList.size());

        for (int i = 0; i < questFileList.size(); i++) {
            buffer.writeUtf(questFileList.get(i));
        }

    }


    public static void handler(ServerQuestList msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.setAvailableServerQuestList(msg.questFileList));
        });

        ctx.get().setPacketHandled(true);
    }
}
