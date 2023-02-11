package dev.zanckor.api.dialog.register;

import com.google.gson.Gson;
import dev.zanckor.api.dialog.abstractdialog.DialogTemplate;
import dev.zanckor.mod.util.MCUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.*;

import static dev.zanckor.mod.QuestApiMain.serverDialogs;

public class DialogRegistry {

    /**
     * Each time that server starts running, <code> registerDialog </code> is called to copy resource's dialog files to minecraft folder.
     */

    static DialogTemplate dialogTemplate;

    public static void registerDialog(String modid) {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

        resourceManager.listResources("dialog", (file) -> {
            if (file.getPath().length() > 7) {
                String fileName = file.getPath().substring(7);
                ResourceLocation resourceLocation = new ResourceLocation(modid, file.getPath());

                if (file.getPath().endsWith(".json")) {
                    read(MCUtil.gson(), resourceLocation);
                    write(MCUtil.gson(), dialogTemplate, fileName);
                } else {
                    throw new RuntimeException("File " + fileName + " in " + file.getPath() + " is not .json");
                }
            }

            return false;
        });
    }


    private static void read(Gson gson, ResourceLocation resourceLocation) {
        try {
            InputStream inputStream = Minecraft.getInstance().getResourceManager().getResource(resourceLocation).get().open();
            dialogTemplate = gson.fromJson(new InputStreamReader(inputStream), DialogTemplate.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void write(Gson gson, DialogTemplate dialogTemplate, String fileName) {
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