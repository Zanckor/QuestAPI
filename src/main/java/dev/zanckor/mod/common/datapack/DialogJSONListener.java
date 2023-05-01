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

public class DialogJSONListener extends SimpleJsonResourceReloadListener {
    public static HashMap<String, JsonObject> datapackDialogList = new HashMap<>();

    public DialogJSONListener(Gson gson, String name) {
        super(gson, name);
    }

    public static void register(AddReloadListenerEvent e) {
        e.addListener(new DialogJSONListener(GsonManager.gson, "questapi/dialog"));
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonElementMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        QuestApiMain.LOGGER.debug("Loaded dialog datapack");

        jsonElementMap.forEach((rl, jsonElement) -> {
            JsonObject obj = jsonElement.getAsJsonObject();

            //Load dialog
            if(obj.get("dialog") != null){
                String dialogID = "." + rl.getPath();
                Path path = Path.of(rl.getNamespace() + dialogID + ".json");

                datapackDialogList.put(path.toString(), obj);
            }
        });
    }
}
