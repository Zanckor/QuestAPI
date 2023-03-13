package dev.zanckor.api.filemanager.npc.entity_type_tag.register;

import com.google.gson.Gson;
import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.FolderManager;
import dev.zanckor.api.filemanager.npc.entity_type_tag.EntityTypeTagDialog;
import dev.zanckor.mod.common.util.GsonManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.LevelResource;

import java.io.*;

import static dev.zanckor.mod.QuestApiMain.compoundTag_List;

public class LoadTagDialogList {

    /**
     * Each time that server starts running, <code> registerNPCTagDialogList </code> is called to copy resource's files to minecraft folder.
     */

    static EntityTypeTagDialog entityTypeTagDialog;

    public static void registerNPCTagDialogList(MinecraftServer server, String modid) throws IOException {
        ResourceManager resourceManager = server.getResourceManager();

        if (compoundTag_List == null) {
            FolderManager.createAPIFolder(server.getWorldPath(LevelResource.ROOT).toAbsolutePath());
        }

        resourceManager.listResources("npc/compound_tag_list", (file) -> {
            if (file.getPath().length() > 22) {
                String fileName = file.getPath().substring(22);
                ResourceLocation resourceLocation = new ResourceLocation(modid, file.getPath());

                if (file.getPath().endsWith(".json")) {
                    read(GsonManager.gson(), resourceLocation, server);
                    write(GsonManager.gson(), entityTypeTagDialog, modid, fileName);
                } else {
                    throw new RuntimeException("File " + fileName + " in " + file.getPath() + " is not .json");
                }
            }

            return false;
        });

        LocateHash.registerDialogPerCompoundTag();
    }


    /*
    public static void registerDatapackNPCDialogList(MinecraftServer server) throws IOException {
        if (entity_type_list == null) {
            FolderManager.createAPIFolder(server.getWorldPath(LevelResource.ROOT).toAbsolutePath());
        }

        for(Map.Entry<String, JsonObject> entry : DialogJSONListener.datapackDialogList.entrySet()){
            FileWriter writer = new FileWriter(String.valueOf(Path.of(serverDialogs + "/" + entry.getKey())));
            writer.write(entry.getValue().toString());
            writer.close();
        }
    }
     */

    private static void read(Gson gson, ResourceLocation resourceLocation, MinecraftServer server) {
        try {
            InputStream inputStream = server.getResourceManager().getResource(resourceLocation).get().open();
            entityTypeTagDialog = gson.fromJson(new InputStreamReader(inputStream), EntityTypeTagDialog.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void write(Gson gson, EntityTypeTagDialog entityTypeDialog, String modid, String fileName) {
        try {
            File file = new File(compoundTag_List.toFile(), modid + "_" + fileName);

            FileWriter writer = new FileWriter(file);

            writer.write(gson.toJson(entityTypeDialog));
            writer.flush();
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}