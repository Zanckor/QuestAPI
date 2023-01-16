package com.zanckor.api.quest_register;

import java.util.HashMap;

public class QuestRegistry {
    private static HashMap<Enum, AbstractQuest> quest_template = new HashMap<>();

    public static void registerQuest(Enum key, AbstractQuest quest) {
        quest_template.put(key, quest);
    }

    public AbstractQuest getQuestTemplate(Enum key) {
        try {
            return quest_template.get(key);
        } catch (NullPointerException e) {
            throw new RuntimeException("Incorrect key: " + key);
        }
    }
}