package com.zanckor.api.dialog.abstractdialog;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public class DialogTemplate {
    public static HashMap<Integer, Path> dialog_location = new HashMap<>();
    private static int global_id;

    List<QuestDialog> dialog;


    public static HashMap<Integer, Path> getDialog_location() {
        return dialog_location;
    }

    public int getGlobal_id() {
        return global_id;
    }

    public List<QuestDialog> getDialog() {
        return dialog;
    }

    public static DialogTemplate createDialog(Path path) {
        DialogTemplate dialogQuest = new DialogTemplate();
        dialogQuest.setGlobal_id(global_id);

        registerDialogLocation(global_id, path);

        return dialogQuest;
    }

    public void setGlobal_id(int global_id) {
        this.global_id = global_id;
        this.global_id++;
    }


    public static class QuestDialog {
        int id;
        String dialogTitle;
        String dialogText;
        DialogRequirement requirements;
        List<DialogOption> options;

        public int getId() {
            return id;
        }

        public String getDialogTitle() {
            return dialogTitle;
        }

        public String getDialogText() {
            return dialogText;
        }

        public DialogRequirement getRequirements() {
            return requirements;
        }

        public List<DialogOption> getOptions() {
            return options;
        }

        private static QuestDialog createDialog(int id, String dialogTitle, String dialogText, DialogRequirement requirements, List<DialogOption> dialogOptions) {
            QuestDialog questDialog = new QuestDialog();
            questDialog.id = id;
            questDialog.dialogTitle = dialogTitle;
            questDialog.dialogText = dialogText;
            questDialog.requirements = requirements;
            questDialog.options = dialogOptions;

            return questDialog;
        }
    }

    public static class DialogRequirement {
        String type;
        int global_id;
        int id;
        String requirement_status;

        public String getType() {
            return type;
        }

        public int getGlobal_id() {
            return global_id;
        }

        public int getId() {
            return id;
        }

        public String getRequirement_status() {
            return requirement_status;
        }

        private static DialogRequirement createRequirement(String type, int global_id, int id, String requirement_status) {
            DialogRequirement requirement = new DialogRequirement();
            requirement.type = type;
            requirement.global_id = global_id;
            requirement.id = id;
            requirement.requirement_status = requirement_status;


            return requirement;
        }
    }

    public static class DialogOption {
        String text;
        String type;
        int global_id;
        int dialog;
        int quest_id;

        public String getText() {
            return text;
        }

        public String getType() {
            return type;
        }

        public int getGlobal_id() {
            return global_id;
        }

        public int getDialog() {
            return dialog;
        }

        public int getQuest_id() {
            return quest_id;
        }

        private static DialogOption createDialogOption(String text, String type, int global_id, int dialog, int quest_id) {
            DialogOption option = new DialogOption();

            option.text = text;
            option.type = type;
            option.global_id = global_id;
            option.dialog = dialog;
            option.quest_id = quest_id;

            return option;
        }
    }


    public static void registerDialogLocation(Integer global_id, Path location) {
        dialog_location.put(global_id, location);
    }

    public static Path getDialogLocation(Integer global_id) {
        return dialog_location.get(global_id);
    }
}