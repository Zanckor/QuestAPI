package dev.zanckor.api.filemanager.dialog.codec;

import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.FileAbstract;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class NPCDialog extends FileAbstract {



    private static String global_id;

    List<QuestDialog> dialog;
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

    public List<QuestDialog> getDialog() {
        return dialog;
    }

    public static NPCDialog createDialog(Path path) throws IOException {
        NPCDialog dialogQuest = new NPCDialog();

        dialogQuest.setGlobal_id(global_id);

        LocateHash.registerDialogLocation(global_id, path);

        return dialogQuest;
    }

    public void setGlobal_id(String global_id) {
        this.global_id = global_id;
    }


    public static class QuestDialog {
        int id;
        String dialogText;
        DialogRequirement requirements;
        List<DialogOption> options;

        public int getId() {
            return id;
        }

        public String getDialogText() {
            return dialogText;
        }

        public DialogRequirement getServerRequirements() {
            return requirements;
        }

        public List<DialogOption> getOptions() {
            return options;
        }

        private static QuestDialog createDialog(int id, String dialogText, DialogRequirement requirements, List<DialogOption> dialogOptions) {
            QuestDialog questDialog = new QuestDialog();
            questDialog.id = id;
            questDialog.dialogText = dialogText;
            questDialog.requirements = requirements;
            questDialog.options = dialogOptions;

            return questDialog;
        }
    }

    public static class DialogRequirement {
        String type;
        String global_id;
        int dialog_id;
        String quest_id;
        String requirement_status;

        public String getType() {
            return type;
        }

        public String getGlobal_id() {
            return global_id;
        }

        public int getDialogId() {
            return dialog_id;
        }

        public String getQuestId() {
            return quest_id;
        }

        public String getRequirement_status() {
            return requirement_status;
        }

        private static DialogRequirement createRequirement(String type, String global_id, int dialog_id, String quest_id, String requirement_status) {
            DialogRequirement requirement = new DialogRequirement();
            requirement.type = type;
            requirement.global_id = global_id;
            requirement.dialog_id = dialog_id;
            requirement.quest_id = quest_id;
            requirement.requirement_status = requirement_status;


            return requirement;
        }
    }

    public static class DialogOption {

        String text;
        String type;
        String global_id;
        int dialog;
        String quest_id;

        public String getText() {
            return text;
        }

        public String getType() {
            return type;
        }

        public String getGlobal_id() {
            return global_id;
        }

        public int getDialog() {
            return dialog;
        }

        public String getQuest_id() {
            return quest_id;
        }

        private static DialogOption createDialogOption(String text, String type, String global_id, int dialog, String quest_id) {
            DialogOption option = new DialogOption();

            option.text = text;
            option.type = type;
            option.global_id = global_id;
            option.dialog = dialog;
            option.quest_id = quest_id;

            return option;
        }
    }
}