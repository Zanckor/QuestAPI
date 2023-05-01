package dev.zanckor.mod.common.datapack;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.common.util.GsonManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class EntityTypeDialogJSONListener extends SimpleJsonResourceReloadListener {
    public static HashMap<String, JsonObject> datapackDialogPerEntityTypeList = new HashMap<>();

    public EntityTypeDialogJSONListener(Gson gson, String name) {
        super(gson, name);
    }

    public static void register(AddReloadListenerEvent e) {
        e.addListener(new EntityTypeDialogJSONListener(GsonManager.gson, "questapi/npc/entity_type_list"));
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonElementMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        QuestApiMain.LOGGER.debug("Loaded list of dialogs loaded via entity type datapack");

        jsonElementMap.forEach((rl, jsonElement) -> {
            JsonObject obj = jsonElement.getAsJsonObject();
            if (obj.get("id") == null) return;

            //Load quest
            if(obj.get("entity_type") != null) {
                String questId = "." + obj.get("id").toString().substring(1, obj.get("id").toString().length() - 1);
                Path path = Path.of(rl.getNamespace() + questId + ".json");

                datapackDialogPerEntityTypeList.put(path.toString(), obj);
            }
        });
    }
}
