package dev.zanckor.mod.common.datapack;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import dev.zanckor.mod.QuestApiMain;
import dev.zanckor.mod.common.util.GsonManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;

import java.util.Map;

public class QuestJSONListener extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();
    public QuestJSONListener() {
        super(GSON, "questapi/quests");
    }

    public static void register(AddReloadListenerEvent e) {
        e.addListener(new QuestJSONListener());
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonElementMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        QuestApiMain.LOGGER.debug("Loaded quest api datapack");
        System.out.println("JSON ELEMENT MAP: " + jsonElementMap.keySet());
        jsonElementMap.forEach((rl, jsonElement) -> {
            JsonObject obj = jsonElement.getAsJsonObject();
            String title = obj.get("title").getAsString();
            System.out.println("Loaded JSON: " + rl.toString() + " with title: " + title);
        })
    }
}
