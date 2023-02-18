package dev.zanckor.mod.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.zanckor.api.filemanager.FileAbstract;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class GsonManager {
    public static Gson gson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

    public static <T extends FileAbstract> FileAbstract getJson(File file, Class<T> fileClass) throws IOException {
        if (!file.exists()) return null;

        FileReader reader = new FileReader(file);
        FileAbstract dialog = GsonManager.gson().fromJson(reader, fileClass);
        reader.close();

        return dialog;
    }
}
