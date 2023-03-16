package dev.zanckor.api.filemanager.dialog.codec;

import dev.zanckor.api.filemanager.FileAbstract;

import java.util.List;

public class ReadDialog extends FileAbstract {

    /**
     * This class allows dev to add dialog to dialog-read.json or get which ones are already read
     *
     * @return Returns a list of dialogs already read by a certain player
     */


    public static class GlobalID {
        String global_id;
        List<DialogID> dialog_id;

        public GlobalID(String id, List<DialogID> dialog_id) {
            this.global_id = id;
            this.dialog_id = dialog_id;
        }

        public String getGlobal_id() {
            return global_id;
        }

        public List<DialogID> getDialog_id() {
            return dialog_id;
        }
    }

    public static class DialogID {
        int dialog_id;

        public DialogID(int id) {
            this.dialog_id = id;
        }

        public int getDialog_id() {
            return dialog_id;
        }
    }
}