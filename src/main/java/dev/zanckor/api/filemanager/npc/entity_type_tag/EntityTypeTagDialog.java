package dev.zanckor.api.filemanager.npc.entity_type_tag;

import dev.zanckor.api.filemanager.FileAbstract;

import java.util.List;

public class EntityTypeTagDialog extends FileAbstract {
    private List<String> entity_type;
    private List<EntityTypeTagDialogCondition> conditions;

    public List<String> getEntity_type() {
        return entity_type;
    }

    public List<EntityTypeTagDialogCondition> getConditions() {
        return conditions;
    }
    public class EntityTypeTagDialogCondition{
        private List<EntityTypeTagDialogNBT> nbt;
        private String logic_gate;
        private List<String> dialog_list;

        public LogicGate getLogic_gate() {
            return LogicGate.valueOf(logic_gate);
        }

        public List<EntityTypeTagDialogNBT> getNbt() {
            return nbt;
        }

        public List<String> getEntity_type() {
            return entity_type;
        }

        public List<String> getDialog_list() {
            return dialog_list;
        }

        public class EntityTypeTagDialogNBT {
            private String tag;
            private String value;

            public String getTag() {
                return tag;
            }

            public String getValue() {
                return value;
            }
        }
    }
}
