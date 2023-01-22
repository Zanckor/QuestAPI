package com.zanckor.mod.network.message;

import com.google.gson.Gson;
import com.zanckor.api.dialog.abstractdialog.DialogTemplate;
import com.zanckor.api.quest.ClientQuestBase;
import com.zanckor.api.database.LocateQuest;
import com.zanckor.api.quest.enumquest.EnumQuestType;
import com.zanckor.api.quest.abstracquest.AbstractQuest;
import com.zanckor.api.quest.register.TemplateRegistry;
import com.zanckor.mod.client.screen.DialogScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

import static com.zanckor.mod.util.MCUtil.getJsonQuest;

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
        Gson gson = new Gson().newBuilder().setPrettyPrinting().create();

        List<Path> questTypeLocation = LocateQuest.getQuestTypeLocation(questType);

        for (Path path : questTypeLocation) {
            File file = path.toFile();
            ClientQuestBase playerQuest = getJsonQuest(file, gson);
            if(playerQuest == null) return;

            if (playerQuest.getQuest_type().equals(questType.toString())) {
                quest.handler(player, gson, file, playerQuest);
            }
        }
    }
}

