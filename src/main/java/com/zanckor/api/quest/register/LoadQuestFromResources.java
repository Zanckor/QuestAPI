package com.zanckor.api.quest.register;

import com.google.gson.Gson;
import com.zanckor.api.quest.ServerQuestBase;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.*;

import static com.zanckor.mod.QuestApiMain.serverQuests;

public class LoadQuestFromResources {
    static ServerQuestBase playerQuest;

    public static void registerQuest(String modid) {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        Gson gson = new Gson().newBuilder().setPrettyPrinting().create();

        resourceManager.listResources("quest", (file) -> {
            if(file.getPath().length() > 7) {
                String fileName = file.getPath().substring(6);
                ResourceLocation resourceLocation = new ResourceLocation(modid, file.getPath());

                if (file.getPath().endsWith(".json")) {
                    read(gson, resourceLocation);
                    write(gson, playerQuest, fileName);
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
            playerQuest = gson.fromJson(new InputStreamReader(inputStream), ServerQuestBase.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void write(Gson gson, ServerQuestBase questTemplate, String fileName) {
        try {
            FileWriter writer = new FileWriter(new File(serverQuests.toFile(), fileName));
            writer.write(gson.toJson(questTemplate));
            writer.flush();
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}