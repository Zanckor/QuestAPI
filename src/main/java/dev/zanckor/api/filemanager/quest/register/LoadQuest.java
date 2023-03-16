package dev.zanckor.api.filemanager.quest.register;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.zanckor.api.filemanager.FolderManager;
import dev.zanckor.api.filemanager.quest.codec.ServerQuest;
import dev.zanckor.mod.common.datapack.QuestJSONListener;
import dev.zanckor.mod.common.util.GsonManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.LevelResource;

import java.io.*;
import java.nio.file.Path;
import java.util.Map;

import static dev.zanckor.mod.QuestApiMain.serverQuests;

public class LoadQuest {

    /**
     * Each time that server starts running, <code> registerquest </code> is called to copy resource's quest files to minecraft folder.
     */


    static ServerQuest playerQuest;

    public static void registerQuest(MinecraftServer server, String modid) {
        ResourceManager resourceManager = server.getResourceManager();

        if (serverQuests == null) {
            FolderManager.createAPIFolder(server.getWorldPath(LevelResource.ROOT).toAbsolutePath());
        }

        resourceManager.listResources("quest", (file) -> {
            if (file.getPath().length() > 7) {
                String fileName = file.getPath().substring(6);
                ResourceLocation resourceLocation = new ResourceLocation(modid, file.getPath());

                if (file.getPath().endsWith(".json")) {
                    read(GsonManager.gson(), resourceLocation, resourceManager);
                    write(GsonManager.gson(), playerQuest, modid + "_" + fileName);
                } else {
                    throw new RuntimeException("File " + fileName + " in " + file.getPath() + " is not .json");
                }
            }

            return false;
        });
    }

    public static void registerDatapackQuest(MinecraftServer server) throws IOException {
        if (serverQuests == null) {
            FolderManager.createAPIFolder(server.getWorldPath(LevelResource.ROOT).toAbsolutePath());
        }

        for(Map.Entry<String, JsonObject> entry : QuestJSONListener.datapackQuestList.entrySet()){
            FileWriter writer = new FileWriter(String.valueOf(Path.of(serverQuests + "/" + entry.getKey())));
            writer.write(entry.getValue().toString());
            writer.close();
        }
    }

    private static void read(Gson gson, ResourceLocation resourceLocation, ResourceManager resourceManager) {
        try {
            InputStream inputStream = resourceManager.getResource(resourceLocation).get().open();
            playerQuest = gson.fromJson(new InputStreamReader(inputStream), ServerQuest.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void write(Gson gson, ServerQuest questTemplate, String fileName) {
        try {
            FileWriter writer = new FileWriter(new File(serverQuests.toFile(), fileName));
            questTemplate.setId(fileName.substring(0, fileName.length() - 5));

            writer.write(gson.toJson(questTemplate));
            writer.flush();
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}