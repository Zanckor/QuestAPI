package dev.zanckor.api.filemanager.quest.register;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.zanckor.api.filemanager.FolderManager;
import dev.zanckor.api.filemanager.quest.codec.server.ServerQuest;
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

        resourceManager.listResources("quest", (path) -> {
            if (path.length() > 7) {
                String fileName = path.substring(6);
                ResourceLocation resourceLocation = new ResourceLocation(modid, path);
                if(!path.contains(modid)) return false;

                if (path.endsWith(".json")) {
                    read(resourceLocation, resourceManager);
                    write(playerQuest, fileName, modid);
                } else {
                    throw new RuntimeException("File " + fileName + " in " + path + " is not .json");
                }
            }

            return false;
        });
    }

    public static void registerDatapackQuest(MinecraftServer server) throws IOException {
        if (serverQuests == null) {
            FolderManager.createAPIFolder(server.getWorldPath(LevelResource.ROOT).toAbsolutePath());
        }

        for (Map.Entry<String, JsonObject> entry : QuestJSONListener.datapackQuestList.entrySet()) {
            FileWriter writer = new FileWriter(String.valueOf(Path.of(serverQuests + "/" + entry.getKey())));
            writer.write(entry.getValue().toString());
            writer.close();
        }
    }

    private static void read(ResourceLocation resourceLocation, ResourceManager resourceManager) {
        try {
            if(!resourceManager.hasResource(resourceLocation)) return;

            InputStream inputStream = resourceManager.getResource(resourceLocation).getInputStream();
            playerQuest = GsonManager.gson.fromJson(new InputStreamReader(inputStream), ServerQuest.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void write(ServerQuest serverQuest, String fileName, String identifier) {
        try {
            if(serverQuest == null) return;

            File file = new File(serverQuests.toFile(), identifier + "." + fileName);
            serverQuest.setId(identifier + "." + fileName.substring(0, fileName.length() - 5));
            GsonManager.writeJson(file, serverQuest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}