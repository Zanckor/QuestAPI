package com.zanckor.api.questregister;

import java.util.HashMap;

public class TemplateRegistry {
    private static HashMap<Enum, AbstractQuest> quest_template = new HashMap<>();

    public static void registerTemplate(Enum key, AbstractQuest quest) {
        quest_template.put(key, quest);
    }

    public static AbstractQuest getQuestTemplate(Enum key) {
        try {
            return quest_template.get(key);
        } catch (NullPointerException e) {
            throw new RuntimeException("Incorrect key: " + key);
        }
    }
}