package com.zanckor.api.dialog.abstractdialog;

import java.util.List;

public class DialogReadTemplate {
    public static class GlobalID {
        int global_id;
        List<DialogID> dialog_id;

        public GlobalID(int id, List<DialogID> dialog_id) {
            this.global_id = id;
            this.dialog_id = dialog_id;
        }

        public int getGlobal_id() {
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