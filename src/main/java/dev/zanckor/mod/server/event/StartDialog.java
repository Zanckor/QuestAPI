package dev.zanckor.mod.server.event;

import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.dialog.abstractdialog.AbstractDialogRequirement;
import dev.zanckor.api.filemanager.dialog.codec.NPCConversation;
import dev.zanckor.api.filemanager.quest.register.QuestTemplateRegistry;
import dev.zanckor.example.common.enumregistry.EnumRegistry;
import dev.zanckor.mod.common.util.GsonManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class StartDialog {

    /**
     * @param player         The player
     * @param globalDialogID Dialog ID of dialog file player will see. Example: collect_items_dialog.json
     * @throws IOException Exception fired when server cannot read json file
     */

    public static void loadDialog(Player player, String globalDialogID, Entity entity) throws IOException {
        Path path = LocateHash.getDialogLocation(globalDialogID);

        File dialogFile = path.toFile();
        LocateHash.currentGlobalDialog.put(player, dialogFile.getName().substring(0, dialogFile.getName().length() - 5));

        NPCConversation dialog = (NPCConversation) GsonManager.getJsonClass(dialogFile, NPCConversation.class);


        for (int dialog_id = dialog.getDialog().size() - 1; dialog_id >= 0; dialog_id--) {
            if (dialog.getDialog().get(dialog_id).getServerRequirements().getType() == null) continue;
            Enum requirementEnum = EnumRegistry.getEnum(dialog.getDialog().get(dialog_id).getServerRequirements().getType(), EnumRegistry.getDialogRequirement());

            AbstractDialogRequirement dialogRequirement = QuestTemplateRegistry.getDialogRequirement(requirementEnum);

            if (dialogRequirement != null && dialogRequirement.handler(player, dialog, dialog_id, entity)) return;
        }
    }
    public static void loadDialog(Player player, String globalDialogID, String resourceLocation) throws IOException {
        Path path = LocateHash.getDialogLocation(globalDialogID);

        File dialogFile = path.toFile();
        LocateHash.currentGlobalDialog.put(player, dialogFile.getName().substring(0, dialogFile.getName().length() - 5));

        NPCConversation dialog = (NPCConversation) GsonManager.getJsonClass(dialogFile, NPCConversation.class);


        for (int dialog_id = dialog.getDialog().size() - 1; dialog_id >= 0; dialog_id--) {
            if (dialog.getDialog().get(dialog_id).getServerRequirements().getType() == null) continue;
            Enum requirementEnum = EnumRegistry.getEnum(dialog.getDialog().get(dialog_id).getServerRequirements().getType(), EnumRegistry.getDialogRequirement());

            AbstractDialogRequirement dialogRequirement = QuestTemplateRegistry.getDialogRequirement(requirementEnum);

            if (dialogRequirement != null && dialogRequirement.handler(player, dialog, dialog_id, resourceLocation)) return;
        }
    }

    public static void loadDialog(Player player, String globalDialogID, Item item) throws IOException {
        Path path = LocateHash.getDialogLocation(globalDialogID);

        File dialogFile = path.toFile();
        LocateHash.currentGlobalDialog.put(player, dialogFile.getName().substring(0, dialogFile.getName().length() - 5));

        NPCConversation dialog = (NPCConversation) GsonManager.getJsonClass(dialogFile, NPCConversation.class);


        for (int dialog_id = dialog.getDialog().size() - 1; dialog_id >= 0; dialog_id--) {
            if (dialog.getDialog().get(dialog_id).getServerRequirements().getType() == null) continue;
            Enum requirementEnum = EnumRegistry.getEnum(dialog.getDialog().get(dialog_id).getServerRequirements().getType(), EnumRegistry.getDialogRequirement());

            AbstractDialogRequirement dialogRequirement = QuestTemplateRegistry.getDialogRequirement(requirementEnum);

            if (dialogRequirement != null && dialogRequirement.handler(player, dialog, dialog_id, item)) return;
        }
    }
}