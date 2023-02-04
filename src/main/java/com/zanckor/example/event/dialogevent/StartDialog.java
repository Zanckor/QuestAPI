package com.zanckor.example.event.dialogevent;

import com.zanckor.api.database.LocateHash;
import com.zanckor.api.dialog.abstractdialog.AbstractDialogRequirement;
import com.zanckor.api.dialog.abstractdialog.DialogTemplate;
import com.zanckor.api.dialog.enumdialog.EnumRequirementType;
import com.zanckor.api.quest.register.TemplateRegistry;
import com.zanckor.mod.QuestApiMain;
import com.zanckor.mod.util.MCUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Mod.EventBusSubscriber(modid = QuestApiMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StartDialog {

    public static void loadDialog(Player player, String globalDialogID) throws IOException {
        Path path = DialogTemplate.getDialogLocation(globalDialogID);

        File dialogFile = path.toFile();
        LocateHash.currentGlobalDialog.put(player, dialogFile.getName().substring(0, dialogFile.getName().length() - 5));

        DialogTemplate dialog = MCUtil.getJsonDialog(dialogFile, MCUtil.gson());


        for (int dialog_id = dialog.getDialog().size() - 1; dialog_id >= 0; dialog_id--) {
            if (dialog.getDialog().get(dialog_id).getRequirements().getType() == null) continue;

            EnumRequirementType requirementType = EnumRequirementType.valueOf(dialog.getDialog().get(dialog_id).getRequirements().getType());
            AbstractDialogRequirement dialogRequirement = TemplateRegistry.getDialogRequirement(requirementType);

            if (dialogRequirement != null && dialogRequirement.handler(player, dialog, dialog_id)) return;
        }
    }
}