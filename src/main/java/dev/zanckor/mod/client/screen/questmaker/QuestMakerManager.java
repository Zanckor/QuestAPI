package dev.zanckor.mod.client.screen.questmaker;

import dev.zanckor.api.filemanager.quest.codec.server.ServerQuest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuestMakerManager {
    public static List<ServerQuest> availableQuests = new ArrayList<>();
    public static HashMap<String, ServerQuest> editingServerQuests = new HashMap<>();

    public static ServerQuest getUnsavedQuest(String id) {
        return editingServerQuests.get(id);
    }

    public static void addQuest(ServerQuest quest){
        editingServerQuests.put(quest.getId(), quest);
    }

    public static void removeChanges(String id){
        editingServerQuests.remove(id);
    }
}