package dev.zanckor.mod.common.network.message.dialogoption;

import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.dialog.codec.NPCConversation;
import dev.zanckor.api.filemanager.dialog.codec.NPCDialog;
import dev.zanckor.mod.common.network.handler.ClientHandler;
import dev.zanckor.mod.common.util.MCUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class DisplayDialog {

    NPCConversation dialogTemplate;

    String identifier;
    int dialogID;
    int optionSize;
    String questDialog;
    HashMap<Integer, List<String>> optionStrings = new HashMap<>();
    HashMap<Integer, List<Integer>> optionIntegers = new HashMap<>();

    UUID npcUUID;


    public DisplayDialog(NPCConversation dialogTemplate, String identifier, int dialogID, Player player, Entity npc) throws IOException {
        this.dialogTemplate = dialogTemplate;
        this.dialogID = dialogID;
        this.npcUUID = npc == null ? player.getUUID() : npc.getUUID();
        this.identifier = identifier != null ? identifier : "questapi";



        LocateHash.currentDialog.put(player, dialogID);
        MCUtil.writeDialogRead(player, dialogID);
    }

    public DisplayDialog(FriendlyByteBuf buffer) {
        npcUUID = buffer.readUUID();
        identifier = buffer.readUtf();

        questDialog = buffer.readUtf();
        optionSize = buffer.readInt();

        for (int optionSizeIndex = 0; optionSizeIndex < optionSize; optionSizeIndex++) {
            List<String> optionStringData = new ArrayList<>();
            List<Integer> optionIntegerData = new ArrayList<>();

            optionStringData.add(buffer.readUtf());
            optionStringData.add(buffer.readUtf());
            optionStringData.add(buffer.readUtf());
            optionStringData.add(buffer.readUtf());


            optionIntegerData.add(buffer.readInt());

            optionStrings.put(optionSizeIndex, optionStringData);
            optionIntegers.put(optionSizeIndex, optionIntegerData);
        }
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
        NPCDialog.QuestDialog dialog = dialogTemplate.getDialog().get(dialogID);

        buffer.writeUUID(npcUUID);
        buffer.writeUtf(identifier);

        buffer.writeUtf(dialog.getDialogText());
        buffer.writeInt(dialog.getOptions().size());

        for (NPCDialog.DialogOption option : dialog.getOptions()) {
            String optionText = option.getText() == null ? "" : option.getText();
            String optionQuestID = option.getQuest_id() == null ? "" : option.getQuest_id();
            String optionGlobalID = option.getGlobal_id() == null ? "" : option.getGlobal_id();

            buffer.writeUtf(optionText);
            buffer.writeUtf(option.getType());
            buffer.writeUtf(optionQuestID);
            buffer.writeUtf(optionGlobalID);
            buffer.writeInt(option.getDialog());
        }
    }


    public static void handler(DisplayDialog msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    ClientHandler.displayDialog(msg.identifier, msg.dialogID, msg.questDialog, msg.optionSize, msg.optionIntegers, msg.optionStrings, msg.npcUUID));
        });

        ctx.get().setPacketHandled(true);
    }
}
