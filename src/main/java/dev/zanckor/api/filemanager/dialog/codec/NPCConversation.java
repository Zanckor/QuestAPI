package dev.zanckor.api.filemanager.dialog.codec;

import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.FileAbstract;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class NPCConversation extends FileAbstract {
    private static String global_id;

    List<NPCDialog.QuestDialog> dialog;
    String identifier;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getGlobal_id() {
        return global_id;
    }

    public List<NPCDialog.QuestDialog> getDialog() {
        return dialog;
    }

    public static NPCConversation createDialog(Path path) throws IOException {
        NPCConversation dialogQuest = new NPCConversation();

        dialogQuest.setGlobal_id(global_id);

        LocateHash.registerDialogLocation(global_id, path);

        return dialogQuest;
    }

    public void setGlobal_id(String global_id) {
        this.global_id = global_id;
    }
}