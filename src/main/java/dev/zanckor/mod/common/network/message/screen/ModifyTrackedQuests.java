package dev.zanckor.mod.common.network.message.screen;

import dev.zanckor.api.filemanager.quest.codec.user.UserQuest;
import dev.zanckor.mod.common.network.handler.ClientHandler;
import dev.zanckor.mod.common.util.GsonManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.io.IOException;
import java.util.function.Supplier;

public class ModifyTrackedQuests {
    private UserQuest userQuest;
    private Boolean addQuest;


    public ModifyTrackedQuests(boolean addQuest, UserQuest userQuest) {
        this.userQuest = userQuest;
        this.addQuest = addQuest;
    }

    public ModifyTrackedQuests(FriendlyByteBuf buffer) {
        try {
            addQuest = buffer.readBoolean();
            userQuest = (UserQuest) GsonManager.getJsonClass(buffer.readUtf(), UserQuest.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
        buffer.writeBoolean(addQuest);
        buffer.writeUtf(GsonManager.gson.toJson(userQuest));
    }


    public static void handler(ModifyTrackedQuests msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.modifyTrackedQuests(msg.addQuest, msg.userQuest));
        });

        ctx.get().setPacketHandled(true);
    }
}

