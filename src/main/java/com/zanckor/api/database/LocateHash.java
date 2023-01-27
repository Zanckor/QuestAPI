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


    public static HashMap<Integer, Path> clientQuestByIDLocation = new HashMap<>();
    public static HashMap<EnumQuestType, List<Path>> clientQuestTypeLocation = new HashMap<>();


    public static void registerQuestTypeLocation(EnumQuestType type, Path path) {
        if (clientQuestTypeLocation.get(type) == null) {
            clientQuestTypeLocation.put(type, new ArrayList<>());
        }

        List<Path> questList = clientQuestTypeLocation.get(type);
        questList.add(path);

        clientQuestTypeLocation.put(type, questList);
    }

    public static void registerQuestByID(Integer id, Path path) {
        clientQuestByIDLocation.put(id, path);
    }


    public static List<Path> getQuestTypeLocation(EnumQuestType type) {
        return clientQuestTypeLocation.get(type);
    }

    public static Path getQuestByID(Integer id) {
        return clientQuestByIDLocation.get(id);
    }

    public static void movePathQuest(Integer id, Path path, EnumQuestType enumQuestType) {
        removeQuest(id, path, enumQuestType);

        registerQuestTypeLocation(enumQuestType, path);
        registerQuestByID(id, path);
    }


    public static void removeQuest(Integer id, Path path, EnumQuestType enumQuestType) {
        List<Path> oldPathList = getQuestTypeLocation(enumQuestType);


        List<Path> newPathList = oldPathList;
        newPathList.remove(path);

        clientQuestTypeLocation.replace(enumQuestType, oldPathList, newPathList);
        clientQuestByIDLocation.remove(id);
    }
}