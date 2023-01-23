package com.zanckor.mod.network.message.dialog;

import com.google.gson.Gson;
import com.zanckor.api.database.LocateHash;
import com.zanckor.api.dialog.abstractdialog.DialogReadTemplate;
import com.zanckor.api.dialog.abstractdialog.DialogTemplate;
import com.zanckor.example.event.QuestEvent;
import com.zanckor.mod.network.ClientHandler;
import com.zanckor.mod.util.MCUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import static com.zanckor.mod.QuestApiMain.getReadDialogs;
import static com.zanckor.mod.QuestApiMain.playerData;

public class DisplayDialog {

    DialogTemplate dialogTemplate;


    int dialogID;
    int optionSize;
    String questDialog;
    HashMap<Integer, List<String>> optionStrings = new HashMap<>();
    HashMap<Integer, List<Integer>> optionIntegers = new HashMap<>();


    public DisplayDialog(DialogTemplate dialogTemplate, int dialogID, Player player) throws IOException {
        this.dialogTemplate = dialogTemplate;
        this.dialogID = dialogID;

        LocateHash.currentDialog.put(player, dialogID);
        MCUtil.writeDialogRead(player, dialogID);
    }

    public DisplayDialog(FriendlyByteBuf buffer) {
        questDialog = buffer.readUtf();

        optionSize = buffer.readInt();
        for (int optionSizeIndex = 0; optionSizeIndex < optionSize; optionSizeIndex++) {
            List<String> optionStringData = new ArrayList<>();
            List<Integer> optionIntegerData = new ArrayList<>();

            optionStringData.add(buffer.readUtf());
            optionStringData.add(buffer.readUtf());


            optionIntegerData.add(buffer.readInt());
            optionIntegerData.add(buffer.readInt());
            optionIntegerData.add(buffer.readInt());

            optionStrings.put(optionSizeIndex, optionStringData);
            optionIntegers.put(optionSizeIndex, optionIntegerData);
        }
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
        DialogTemplate.QuestDialog dialog = dialogTemplate.getDialog().get(dialogID);
        buffer.writeUtf(dialog.getDialogText());

        buffer.writeInt(dialog.getOptions().size());
        for (DialogTemplate.DialogOption option : dialog.getOptions()) {
            buffer.writeUtf(option.getText());
            buffer.writeUtf(option.getType());
            buffer.writeInt(option.getGlobal_id());
            buffer.writeInt(option.getQuest_id());
            buffer.writeInt(option.getDialog());
        }
    }


    public static void handler(DisplayDialog msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {

            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.displayDialog(msg.dialogID,
                    msg.questDialog,
                    msg.optionSize, msg.optionIntegers, msg.optionStrings));
        });

        ctx.get().setPacketHandled(true);
    }
}

