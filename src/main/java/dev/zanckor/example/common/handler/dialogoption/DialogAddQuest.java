package dev.zanckor.example.common.handler.dialogoption;

import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.dialog.codec.ServerDialog;
import dev.zanckor.api.filemanager.dialog.abstractdialog.AbstractDialogOption;
import dev.zanckor.api.filemanager.quest.codec.ServerQuest;
import dev.zanckor.api.filemanager.quest.codec.UserQuest;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractQuestRequirement;
import dev.zanckor.api.filemanager.quest.register.QuestTemplateRegistry;
import dev.zanckor.example.common.enumregistry.enumdialog.EnumOptionType;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestRequirement;
import dev.zanckor.mod.common.network.handler.ClientHandler;
import dev.zanckor.mod.common.network.SendQuestPacket;
import dev.zanckor.mod.common.network.message.dialogoption.CloseDialog;
import dev.zanckor.mod.common.util.GsonManager;
import dev.zanckor.mod.common.util.MCUtil;
import dev.zanckor.mod.common.util.Timer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static dev.zanckor.mod.QuestApiMain.*;

public class DialogAddQuest extends AbstractDialogOption {

    /**
     * When player clicks on an option which type is "ADD_QUEST" will try to give it if player has the requirements.
     * In case that player obtains the quest, a new file will be written on his folder data.
     *
     * @param player    The player
     * @param dialog    DialogTemplate class with all dialog data
     * @param option_id DialogOption ID, Returns the object inside the List< DialogOption >. This is not a parameter inside the .json file
     * @throws IOException Exception fired when server cannot read json file
     */

    @Override
    public void handler(Player player, ServerDialog dialog, int option_id, Entity npc) throws IOException {
        int currentDialog = LocateHash.currentDialog.get(player);
        ServerDialog.DialogOption option = dialog.getDialog().get(currentDialog).getOptions().get(option_id);
        String quest = option.getQuest_id() + ".json";
        Path userFolder = Paths.get(playerData.toString(), player.getUUID().toString());

        if (!(option.getType().equals(EnumOptionType.ADD_QUEST.toString()))) return;
        if (MCUtil.hasQuest(quest, userFolder)) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientHandler::closeDialog);
            return;
        }


        for (File file : serverQuests.toFile().listFiles()) {
            if (!(file.getName().equals(quest))) continue;

            Path path = Paths.get(getActiveQuest(userFolder).toString(), "\\", file.getName());
            ServerQuest serverQuest = (ServerQuest) GsonManager.getJsonClass(file, ServerQuest.class);

            //Checks all quest requirements and return if player hasn't any requirement
            for (int requirementIndex = 0; requirementIndex < serverQuest.getRequirements().size(); requirementIndex++) {
                AbstractQuestRequirement requirement = QuestTemplateRegistry.getQuestRequirement(EnumQuestRequirement.valueOf(serverQuest.getRequirements().get(requirementIndex).getType()));

                if (!requirement.handler(player, serverQuest, requirementIndex)) {
                    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientHandler::closeDialog);
                    return;
                }
            }

            FileWriter writer = new FileWriter(path.toFile());
            UserQuest userQuest = UserQuest.createQuest(serverQuest, path);
            GsonManager.gson().toJson(userQuest, writer);
            writer.close();

            if (userQuest.hasTimeLimit()) {
                Timer.updateCooldown(player.getUUID(), option.getQuest_id(), userQuest.getTimeLimitInSeconds());
            }

            LocateHash.registerQuestByID(option.getQuest_id(), path);

            break;
        }

        SendQuestPacket.TO_CLIENT(player, new CloseDialog());
    }
}
