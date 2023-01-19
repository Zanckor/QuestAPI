package com.zanckor.mod.network.message;

import com.google.gson.Gson;
import com.zanckor.api.EnumQuestType;
import com.zanckor.api.questregister.abstrac.AbstractQuest;
import com.zanckor.api.questregister.register.TemplateRegistry;
import com.zanckor.api.questregister.abstrac.PlayerQuest;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

import static com.zanckor.mod.QuestApiMain.getActiveQuest;
import static com.zanckor.mod.QuestApiMain.playerData;

public class QuestDataPacket {

    EnumQuestType quest;

    public QuestDataPacket(EnumQuestType quest) {
        //System.out.println("QUEST: " + quest);

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
        Gson gson = new Gson().newBuilder().setPrettyPrinting().create();
        Path userFolder = Paths.get(playerData.toString(), player.getUUID().toString());

        for (File file : getActiveQuest(userFolder).toFile().listFiles()) {
            FileReader initialReader = new FileReader(file);
            PlayerQuest playerQuest = gson.fromJson(initialReader, PlayerQuest.class);
            initialReader.close();

            if (playerQuest.getQuest_type().equals(questType.toString())) {
                quest.handler(player, gson, file, playerQuest);
            }
        }
    }
}

