package com.zanckor.api.quest.register;

import com.zanckor.api.dialog.abstractdialog.AbstractDialog;
import com.zanckor.api.quest.abstracquest.AbstractQuest;
import com.zanckor.api.quest.abstracquest.AbstractRequirement;
import com.zanckor.api.quest.abstracquest.AbstractReward;

import java.util.HashMap;

public class TemplateRegistry {
    private static HashMap<Enum, AbstractQuest> quest_template = new HashMap<>();
    private static HashMap<Enum, AbstractReward> quest_reward = new HashMap<>();
    private static HashMap<Enum, AbstractRequirement> quest_requirement = new HashMap<>();
    private static HashMap<Enum, AbstractDialog> dialog_template = new HashMap<>();

    public static void registerQuestTemplate(Enum key, AbstractQuest quest) {
        quest_template.put(key, quest);
    }

    public static AbstractQuest getQuestTemplate(Enum key) {
        try {
            return quest_template.get(key);
        } catch (NullPointerException e) {
            throw new RuntimeException("Incorrect quest key: " + key);
        }
    }


    public static void registerDialogTemplate(Enum key, AbstractDialog dialog) {
        dialog_template.put(key, dialog);
    }

    public static AbstractDialog getDialogTemplate(Enum key) {
        try {
            return dialog_template.get(key);
        } catch (NullPointerException e) {
            throw new RuntimeException("Incorrect quest key: " + key);
        }
    }


    public static void registerReward(Enum key, AbstractReward reward) {
        quest_reward.put(key, reward);
    }

    public static AbstractReward getQuestReward(Enum key) {
        try {
            return quest_reward.get(key);
        } catch (NullPointerException e) {
            throw new RuntimeException("Incorrect reward key: " + key);
        }
    }


    public static void registerRequirement(Enum key, AbstractRequirement requirement) {
        quest_requirement.put(key, requirement);
    }

    public static AbstractRequirement getQuestRequirement(Enum key) {
        try {
            return quest_requirement.get(key);
        } catch (NullPointerException e) {
            throw new RuntimeException("Incorrect requirement key: " + key);
        }
    }
}