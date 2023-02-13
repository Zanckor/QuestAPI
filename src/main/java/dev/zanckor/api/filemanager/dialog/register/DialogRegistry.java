package dev.zanckor.api.filemanager.dialog.register;

import com.google.gson.Gson;
import dev.zanckor.api.filemanager.dialog.ServerDialog;
import dev.zanckor.mod.common.util.GsonManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.*;

import static dev.zanckor.mod.QuestApiMain.serverDialogs;

public class DialogRegistry {

    /**
     * Each time that server starts running, <code> registerDialog </code> is called to copy resource's dialog files to minecraft folder.
     */

    static ServerDialog dialogTemplate;

    public static void registerDialog(MinecraftServer server, String modid) {
        ResourceManager resourceManager = server.getResourceManager();

        resourceManager.listResources("dialog", (file) -> {
            if (file.getPath().length() > 7) {
                String fileName = file.getPath().substring(7);
                ResourceLocation resourceLocation = new ResourceLocation(modid, file.getPath());

                if (file.getPath().endsWith(".json")) {
                    read(GsonManager.gson(), resourceLocation, server);
                    write(GsonManager.gson(), dialogTemplate, modid + "_" + fileName);
                } else {
                    throw new RuntimeException("File " + fileName + " in " + file.getPath() + " is not .json");
                }
            }

            return false;
        });
    }


    private static void read(Gson gson, ResourceLocation resourceLocation, MinecraftServer server) {
        try {
            InputStream inputStream = server.getResourceManager().getResource(resourceLocation).get().open();
            dialogTemplate = gson.fromJson(new InputStreamReader(inputStream), ServerDialog.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void write(Gson gson, ServerDialog dialogTemplate, String fileName) {
        try {
            File file = new File(serverDialogs.toFile(), fileName);

            FileWriter writer = new FileWriter(file);
            writer.write(gson.toJson(dialogTemplate));
            writer.flush();
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}