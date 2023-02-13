package dev.zanckor.mod.common.network.message.screen;

import dev.zanckor.api.filemanager.quest.UserQuest;
import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.common.network.ClientHandler;
import dev.zanckor.mod.common.util.GsonManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class QuestList {

    List<String> id = new ArrayList<>();
    List<String> title = new ArrayList<>();

    int listSize;

    public QuestList(UUID playerUUID) throws IOException {
        File[] activeQuests = QuestApiMain.getActiveQuest(QuestApiMain.getUserFolder(playerUUID)).toFile().listFiles();


        for (File file : activeQuests) {
            UserQuest userQuest = (UserQuest) GsonManager.getJson(file, UserQuest.class);

            title.add(userQuest.getTitle());
            id.add(userQuest.getId());
        }
    }

    public QuestList(FriendlyByteBuf buffer) {
        listSize = buffer.readInt();

        for (int i = 0; i < listSize; i++) {
            id.add(buffer.readUtf());
            title.add(buffer.readUtf());
        }
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
        buffer.writeInt(title.size());


        for (int i = 0; i < title.size(); i++) {
            buffer.writeUtf(id.get(i));
            buffer.writeUtf(title.get(i));
        }
    }


    public static void handler(QuestList msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.displayQuestList(msg.id, msg.title));
        });

        ctx.get().setPacketHandled(true);
    }
}