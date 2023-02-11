package dev.zanckor.api.database;

import net.minecraft.world.entity.player.Player;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocateHash {

    /**
     * This class stores server quests and dialogs as temporary data to improve access time.
     *
     * <p>
     *      <code> currentDialog: </code> Stores dialog id of a text displayed to a player. It's equal to the id in the dialog.json file.
     * </p>
     *
     * <p>
     *      <code> currentGlobalDialog: </code> Stores the path to a json file that have dialog information. Example: collect_items_dialog.json
     * </p>
     *
     * <p>
     *      <code> clientQuestByIDLocation: </code> A path to access quests faster by quest id. Only active quests are stored, not completed or uncompleted quests.
     * </p>
     *
     * <p>
     *      <code> clientQuestTypeLocation: </code> A list of paths referenced to an Enum that stores all quests of Enum type.
     * </p>
     *
     * <p>
     *      <code> dialogLocation </code>: A path referenced to dialog ID.
     * </p>
     *
     * <p> <p>
     *     As a developer you shouldn't add stuff here to registry more types of quests, dialogs, rewards, etc.
     * </p>
     *
     * @return clientQuestTypeLocation returns a list<Path> of all quests of Enum type
     *
     */


    public static HashMap<Player, Integer> currentDialog = new HashMap<>();
    public static HashMap<Player, String> currentGlobalDialog = new HashMap<>();


    public static HashMap<String, Path> clientQuestByIDLocation = new HashMap<String, Path>();
    public static HashMap<Enum, List<Path>> clientQuestTypeLocation = new HashMap<>();

    public static HashMap<String, Path> dialogLocation = new HashMap<>();


    public static void registerQuestTypeLocation(Enum type, Path path) {
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


    public static List<Path> getQuestTypeLocation(Enum type) {
        return clientQuestTypeLocation.get(type);
    }

    public static Path getQuestByID(String id) {
        return clientQuestByIDLocation.get(id);
    }

    public static void movePathQuest(String id, Path newPath, Enum enumQuestType) {
        removeQuest(id, newPath, enumQuestType);

        registerQuestTypeLocation(enumQuestType, newPath);
        registerQuestByID(id, newPath);
    }


    public static void removeQuest(String id, Path oldPath, Enum enumQuestType) {
        List<Path> oldPathList = getQuestTypeLocation(enumQuestType);

        List<Path> newPathList = oldPathList;

        newPathList.removeIf(listId -> (listId.toString().contains(id + ".json")));

        clientQuestTypeLocation.replace(enumQuestType, oldPathList, newPathList);
        clientQuestByIDLocation.remove(id);
    }


    public static void registerDialogLocation(String global_id, Path location) {
        dialogLocation.put(global_id.substring(0, global_id.length() - 5), location);
    }

    public static Path getDialogLocation(String global_id) {
        return dialogLocation.get(global_id);
    }
}