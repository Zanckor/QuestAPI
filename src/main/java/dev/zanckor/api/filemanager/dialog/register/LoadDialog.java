package dev.zanckor.api.filemanager.dialog.register;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.zanckor.api.filemanager.FolderManager;
import dev.zanckor.api.filemanager.dialog.codec.NPCConversation;
import dev.zanckor.mod.common.datapack.DialogJSONListener;
import dev.zanckor.mod.common.util.GsonManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.LevelResource;

import java.io.*;
import java.nio.file.Path;
import java.util.Map;

import static dev.zanckor.mod.QuestApiMain.serverDialogs;

public class LoadDialog {

    /**
     * Each time that server starts running, <code> registerDialog </code> is called to copy resource's dialog files to minecraft folder.
     */

    static NPCConversation dialogTemplate;

    public static void registerDialog(MinecraftServer server, String modid) {
        ResourceManager resourceManager = server.getResourceManager();

        if (serverDialogs == null) {
            FolderManager.createAPIFolder(server.getWorldPath(LevelResource.ROOT).toAbsolutePath());
        }

        resourceManager.listResources("dialog", (file) -> {
            if (file.getPath().length() > 7) {
                String fileName = file.getPath().substring(7);
                ResourceLocation resourceLocation = new ResourceLocation(modid, file.getPath());
                if(!modid.equals(file.getNamespace())) return false;

                if (file.getPath().endsWith(".json")) {
                    read(resourceLocation, server);
                    write(dialogTemplate, modid, fileName);
                } else {
                    throw new RuntimeException("File " + fileName + " in " + file.getPath() + " is not .json");
                }
            }

            return false;
        });
    }


    public static void registerDatapackDialog(MinecraftServer server) throws IOException {
        if (serverDialogs == null) {
            FolderManager.createAPIFolder(server.getWorldPath(LevelResource.ROOT).toAbsolutePath());
        }

        for(Map.Entry<String, JsonObject> entry : DialogJSONListener.datapackDialogList.entrySet()){
            FileWriter writer = new FileWriter(String.valueOf(Path.of(serverDialogs + "/" + entry.getKey())));
            writer.write(entry.getValue().toString());
            writer.close();
        }
    }

    private static void read(ResourceLocation resourceLocation, MinecraftServer server) {
        try {
            if(!server.getResourceManager().getResource(resourceLocation).isPresent()) return;

            InputStream inputStream = server.getResourceManager().getResource(resourceLocation).get().open();
            dialogTemplate = GsonManager.gson.fromJson(new InputStreamReader(inputStream), NPCConversation.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void write(NPCConversation dialogTemplate, String identifier, String fileName) {
        try {
            if(dialogTemplate == null) return;

            File file = new File(serverDialogs.toFile(), identifier + "." + fileName);

            if (dialogTemplate.getIdentifier() == null || dialogTemplate.getIdentifier().isEmpty()) {
                dialogTemplate.setIdentifier(identifier);
            }

            GsonManager.writeJson(file, dialogTemplate);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}