package dev.zanckor.api.filemanager.quest.register;

import com.google.gson.Gson;
import dev.zanckor.api.filemanager.quest.ServerQuest;
import dev.zanckor.mod.QuestApiMain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.*;

public class LoadQuestFromResources {

    /**
     * Each time that server starts running, <code> registerquest </code> is called to copy resource's quest files to minecraft folder.
     */


    static ServerQuest playerQuest;

    public static void registerQuest(MinecraftServer server, String modid) {
        ResourceManager resourceManager = server.getResourceManager();

        resourceManager.listResources("quest", (file) -> {
            if(file.getPath().length() > 7) {
                String fileName = file.getPath().substring(6);
                ResourceLocation resourceLocation = new ResourceLocation(modid, file.getPath());

                if (file.getPath().endsWith(".json")) {
                    read(GsonManager.gson(), resourceLocation, server);
                    write(GsonManager.gson(), playerQuest, modid + "_" + fileName);
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
            playerQuest = gson.fromJson(new InputStreamReader(inputStream), ServerQuest.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void write(Gson gson, ServerQuest questTemplate, String fileName) {
        try {
            FileWriter writer = new FileWriter(new File(QuestApiMain.serverQuests.toFile(), fileName));
            questTemplate.setId(fileName.substring(0, fileName.length() - 5));

            writer.write(gson.toJson(questTemplate));
            writer.flush();
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}