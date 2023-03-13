package dev.zanckor.api.filemanager.npc.entity_type;

import dev.zanckor.api.filemanager.FileAbstract;

import java.util.List;

public class EntityTypeDialog extends FileAbstract {
    private List<String> entity_type;
    private List<String> dialog_list;

    public List<String> getEntity_type() {
        return entity_type;
    }

    public List<String> getDialog_list() {
        return dialog_list;
    }
}
