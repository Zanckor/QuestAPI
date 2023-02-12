package dev.zanckor.mod.network.message.quest;

import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.quest.ClientQuestBase;
import dev.zanckor.api.quest.abstracquest.AbstractQuest;
import dev.zanckor.example.enumregistry.enumquest.EnumQuestType;
import dev.zanckor.api.quest.register.TemplateRegistry;
import dev.zanckor.mod.util.MCUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

import static dev.zanckor.mod.util.MCUtil.getJsonClientQuest;

public class QuestDataPacket {

    EnumQuestType quest;

    public QuestDataPacket(EnumQuestType quest) {
        this.quest = quest;
    }

    public QuestDataPacket(FriendlyByteBuf buffer) {
        this.quest = buffer.readEnum(EnumQuestType.class);
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
        buffer.writeEnum(quest);
    }


    public static void handler(QuestDataPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                runOnThread(msg.quest, ctx);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        ctx.get().setPacketHandled(true);
    }

    private static void runOnThread(EnumQuestType questType, Supplier<NetworkEvent.Context> ctx) throws IOException {
        AbstractQuest quest = TemplateRegistry.getQuestTemplate(questType);
        Player player = ctx.get().getSender();

        if (quest == null) return;
        List<Path> questTypeLocation = LocateHash.getQuestTypeLocation(questType);

        for (int i = 0; i < questTypeLocation.size(); i++) {

            Path path = questTypeLocation.get(i).toAbsolutePath();
            ClientQuestBase playerQuest = getJsonClientQuest(path.toFile());

            if (playerQuest == null || playerQuest.isCompleted()) continue;


            if (playerQuest.getQuest_type().equals(questType.toString())) {
                quest.handler(player, MCUtil.gson(), path.toFile(), playerQuest);
            }
        }
    }
}