package com.zanckor.api.database;

import com.zanckor.api.quest.enumquest.EnumQuestType;
import net.minecraft.world.entity.player.Player;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocateHash {
    public static HashMap<Player, Integer> currentDialog = new HashMap<>();
    public static HashMap<Player, Integer> currentGlobalDialog = new HashMap<>();


    public static HashMap<Integer, Path> quest_by_ID_location = new HashMap<>();
    public static HashMap<EnumQuestType, List<Path>> quest_type_location = new HashMap<>();

    public static void registerQuestTypeLocation(EnumQuestType type, Path path) {
        if (quest_type_location.get(type) == null) {
            quest_type_location.put(type, new ArrayList<>());
        }

        List<Path> questList = quest_type_location.get(type);
        questList.add(path);

        quest_type_location.put(type, questList);
    }

    public static void registerQuestByID(Integer id, Path path) {
        quest_by_ID_location.put(id, path);
    }


    public static List<Path> getQuestTypeLocation(EnumQuestType type) {
        return quest_type_location.get(type);
    }

    public static Path getQuestByID(Integer id) {
        return quest_by_ID_location.get(id);
    }

    public static void removeQuest(Integer id, Path path, EnumQuestType enumQuestType){
        quest_type_location.remove(enumQuestType, id);
        quest_by_ID_location.remove(id);
    }
}
