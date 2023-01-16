package com.zanckor.api.quest_register;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static com.zanckor.mod.QuestApiMain.*;

public abstract class AbstractQuest {

    public abstract void handler(Player player, Gson gson, File file) throws IOException;


    /*
        public abstract void handler(int id, String title,
                                        List<String> quest_target, List<Integer> target_quantity,
                                        boolean hasTimeLimit, int timeLimitInSeconds,
                                        Enum requirements_type, int requirements_min, int requirements_max,
                                        Enum reward_type, List<String> reward, List<Integer> reward_quantity) throws IOException;

    public static void createQuest(QuestTemplate questTemplate) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        FileWriter writer = new FileWriter(serverDirectory.toFile());
        gson.toJson(questTemplate, writer);
        writer.flush();
        writer.close();
    }
     */
}
