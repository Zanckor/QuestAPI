package dev.zanckor.mod.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.zanckor.api.filemanager.FileAbstract;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GsonManager {
    public static Gson gson = new Gson();

    public static <T extends FileAbstract> FileAbstract getJsonClass(File file, Class<T> fileClass) throws IOException {
        if (!file.exists()) return null;

        FileReader reader = new FileReader(file);
        FileAbstract dialog = GsonManager.gson.fromJson(reader, fileClass);
        reader.close();

        return dialog;
    }

    public static <T extends FileAbstract> FileAbstract getJsonClass(String file, Class<T> fileClass) throws IOException {
        return GsonManager.gson.fromJson(file, fileClass);
    }

    public static <T extends FileAbstract> void writeJson(File file, T fileClass) throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write(GsonManager.gson.toJson(fileClass));
        writer.flush();
        writer.close();
    }
}
