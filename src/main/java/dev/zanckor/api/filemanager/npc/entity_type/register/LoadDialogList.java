package dev.zanckor.api.filemanager.npc.entity_type.register;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.FolderManager;
import dev.zanckor.api.filemanager.npc.entity_type.codec.EntityTypeDialog;
import dev.zanckor.mod.common.datapack.DialogJSONListener;
import dev.zanckor.mod.common.util.GsonManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.LevelResource;

import java.io.*;
import java.nio.file.Path;
import java.util.Map;

import static dev.zanckor.mod.QuestApiMain.entity_type_list;
import static dev.zanckor.mod.QuestApiMain.serverDialogs;
import static dev.zanckor.mod.common.datapack.EntityTypeDialogJSONListener.datapackDialogPerEntityTypeList;

public class LoadDialogList {

    /**
     * Each time that server starts running, <code> registerNPCDialogList </code> is called to copy resource's files to minecraft folder.
     */

    static EntityTypeDialog entityTypeDialog;

    public static void registerNPCDialogList(MinecraftServer server, String modid) throws IOException {
        ResourceManager resourceManager = server.getResourceManager();

        if (entity_type_list == null) {
            FolderManager.createAPIFolder(server.getWorldPath(LevelResource.ROOT).toAbsolutePath());
        }

        resourceManager.listResources("npc/entity_type_list", (file) -> {
            if (file.getPath().length() > 22) {
                String fileName = file.getPath().substring(21);
                ResourceLocation resourceLocation = new ResourceLocation(modid, file.getPath());

                if (file.getPath().endsWith(".json")) {
                    read(GsonManager.gson(), resourceLocation, server);
                    write(GsonManager.gson(), entityTypeDialog, modid, fileName);
                } else {
                    throw new RuntimeException("File " + fileName + " in " + file.getPath() + " is not .json");
                }
            }

            return false;
        });

        LocateHash.registerDialogPerEntityType();
    }


    public static void registerDatapackDialogList(MinecraftServer server) throws IOException {
        if (entity_type_list == null) {
            FolderManager.createAPIFolder(server.getWorldPath(LevelResource.ROOT).toAbsolutePath());
        }

        for (Map.Entry<String, JsonObject> entry : datapackDialogPerEntityTypeList.entrySet()) {
            FileWriter writer = new FileWriter(String.valueOf(Path.of(entity_type_list + "/" + entry.getKey())));
            writer.write(entry.getValue().toString());
            writer.close();
        }
    }


    private static void read(Gson gson, ResourceLocation resourceLocation, MinecraftServer server) {
        try {
            InputStream inputStream = server.getResourceManager().getResource(resourceLocation).get().open();
            entityTypeDialog = gson.fromJson(new InputStreamReader(inputStream), EntityTypeDialog.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void write(Gson gson, EntityTypeDialog entityTypeDialog, String modid, String fileName) {
        try {
            File file = new File(entity_type_list.toFile(), modid + "." + fileName);

            FileWriter writer = new FileWriter(file);

            writer.write(gson.toJson(entityTypeDialog));
            writer.flush();
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}