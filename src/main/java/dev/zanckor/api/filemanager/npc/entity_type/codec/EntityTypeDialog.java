package dev.zanckor.api.filemanager.npc.entity_type.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.zanckor.api.filemanager.FileAbstract;

import java.util.List;

public class EntityTypeDialog extends FileAbstract {
    public static final Codec<EntityTypeDialog> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("id").forGetter(EntityTypeDialog::getId),
                    Codec.STRING.listOf().fieldOf("entity_type").forGetter(EntityTypeDialog::getEntity_type),
                    Codec.STRING.listOf().fieldOf("dialog_list").forGetter(EntityTypeDialog::getDialog_list)
            ).apply(instance, (id, entityType, dialogList) -> new EntityTypeDialog()));


    private String id;
    private List<String> entity_type;
    private List<String> dialog_list;

    public List<String> getEntity_type() {
        return entity_type;
    }

    public List<String> getDialog_list() {
        return dialog_list;
    }

    public String getId() {
        return id;
    }
}
