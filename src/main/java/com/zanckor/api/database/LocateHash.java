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


    public static HashMap<String, Path> clientQuestByIDLocation = new HashMap<String, Path>();
    public static HashMap<EnumQuestType, List<Path>> clientQuestTypeLocation = new HashMap<>();


    public static void registerQuestTypeLocation(EnumQuestType type, Path path) {
        if (clientQuestTypeLocation.get(type) == null) {
            clientQuestTypeLocation.put(type, new ArrayList<>());
        }

        List<Path> questList = clientQuestTypeLocation.get(type);
        questList.add(path);

        clientQuestTypeLocation.put(type, questList);
    }

    public static void registerQuestByID(String id, Path path) {
        clientQuestByIDLocation.put(id, path);
    }


    public static List<Path> getQuestTypeLocation(EnumQuestType type) {
        return clientQuestTypeLocation.get(type);
    }

    public static Path getQuestByID(String id) {
        return clientQuestByIDLocation.get(id);
    }

    public static void movePathQuest(String id, Path newPath, EnumQuestType enumQuestType) {
        removeQuest(id, newPath, enumQuestType);

        registerQuestTypeLocation(enumQuestType, newPath);
        registerQuestByID(id, newPath);
    }


    public static void removeQuest(String id, Path oldPath, EnumQuestType enumQuestType) {
        List<Path> oldPathList = getQuestTypeLocation(enumQuestType);

        List<Path> newPathList = oldPathList;

        newPathList.removeIf(listId -> (listId.toString().contains(id + ".json")));

        clientQuestTypeLocation.replace(enumQuestType, oldPathList, newPathList);
        clientQuestByIDLocation.remove(id);
    }
}