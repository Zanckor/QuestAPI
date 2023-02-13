package dev.zanckor.mod.common.network.message.screen;

import dev.zanckor.api.filemanager.quest.UserQuest;
import dev.zanckor.mod.common.network.ClientHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class QuestTracked {
    private String id;
    private String title;
    private String quest_type;
    private boolean quest_completed;

    private Integer quest_target_size;
    private List<String> quest_target = new ArrayList<>();

    private Integer target_quantity_size;
    private List<Integer> target_quantity = new ArrayList<>();
    private List<Integer> target_current_quantity = new ArrayList<>();
    private boolean hasTimeLimit;
    private int timeLimitInSeconds;


    public QuestTracked(UserQuest clientQuestBase) {
        id = clientQuestBase.getId();
        title = clientQuestBase.getTitle();
        quest_type = clientQuestBase.getQuest_type();
        quest_completed = clientQuestBase.isCompleted();
        quest_target = clientQuestBase.getQuest_target();
        target_quantity = clientQuestBase.getTarget_quantity();
        target_current_quantity = clientQuestBase.getTarget_current_quantity();
        hasTimeLimit = clientQuestBase.hasTimeLimit();
        timeLimitInSeconds = clientQuestBase.getTimeLimitInSeconds();
    }

    public QuestTracked(FriendlyByteBuf buffer) {
        title = buffer.readUtf();
        id = buffer.readUtf();
        quest_type = buffer.readUtf();
        quest_completed = buffer.readBoolean();

        quest_target_size = buffer.readInt();
        target_quantity_size = buffer.readInt();

        for(int i = 0; i < quest_target_size; i++){
            quest_target.add(buffer.readUtf());
        }

        for(int i = 0; i < target_quantity_size; i++){
            target_quantity.add(buffer.readInt());
            target_current_quantity.add(buffer.readInt());
        }

        hasTimeLimit = buffer.readBoolean();
        timeLimitInSeconds = buffer.readInt();
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
        buffer.writeUtf(title);
        buffer.writeUtf(id);
        buffer.writeUtf(quest_type);
        buffer.writeBoolean(quest_completed);

        buffer.writeInt(quest_target.size());
        buffer.writeInt(target_quantity.size());

        for(int i = 0; i < quest_target.size(); i++){
            buffer.writeUtf(quest_target.get(i));
        }

        for(int i = 0; i < target_quantity.size(); i++){
            buffer.writeInt(target_quantity.get(i));
            buffer.writeInt(target_current_quantity.get(i));
        }

        buffer.writeBoolean(hasTimeLimit);
        buffer.writeInt(timeLimitInSeconds);
    }


    public static void handler(QuestTracked msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.questTracked(msg.id, msg.title, msg.quest_type,
                    msg.quest_completed, msg.quest_target, msg.target_quantity, msg.target_current_quantity,
                    msg.hasTimeLimit, msg.timeLimitInSeconds));
        });

        ctx.get().setPacketHandled(true);
    }
}

