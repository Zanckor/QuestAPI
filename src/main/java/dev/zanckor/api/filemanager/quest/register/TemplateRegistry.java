package dev.zanckor.api.filemanager.quest.register;

import dev.zanckor.api.filemanager.dialog.abstractdialog.AbstractDialogOption;
import dev.zanckor.api.filemanager.dialog.abstractdialog.AbstractDialogRequirement;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractReward;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractQuest;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractQuestRequirement;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractTargetType;

import java.util.HashMap;

public class TemplateRegistry {

    /**
     * This class stores quest types, reward types, requirement types... as temporary data to improve access time.
     * You should use these methods to add your own quests.
     */


    private static HashMap<Enum, AbstractQuest> quest_template = new HashMap<>();
    private static HashMap<Enum, AbstractReward> quest_reward = new HashMap<>();
    private static HashMap<Enum, AbstractQuestRequirement> quest_requirement = new HashMap<>();
    private static HashMap<Enum, AbstractDialogRequirement> dialog_requirement = new HashMap<>();
    private static HashMap<Enum, AbstractDialogOption> dialog_template = new HashMap<>();

    private static HashMap<Enum, AbstractTargetType> target_type = new HashMap<>();

    public static void registerQuest(Enum key, AbstractQuest quest) {
        quest_template.put(key, quest);
    }

    public static AbstractQuest getQuestTemplate(Enum key) {
        try {
            return quest_template.get(key);
        } catch (NullPointerException e) {
            throw new RuntimeException("Incorrect quest key: " + key);
        }
    }

    public static HashMap<Enum, AbstractQuest> getAllQuestTemplates(){
        return quest_template;
    }



    public static void registerDialog(Enum key, AbstractDialogOption dialog) {
        dialog_template.put(key, dialog);
    }

    public static AbstractDialogOption getDialogTemplate(Enum key) {
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


    public static void registerQuestRequirement(Enum key, AbstractQuestRequirement requirement) {
        quest_requirement.put(key, requirement);
    }

    public static AbstractQuestRequirement getQuestRequirement(Enum key) {
        try {
            return quest_requirement.get(key);
        } catch (NullPointerException e) {
            throw new RuntimeException("Incorrect requirement key: " + key);
        }
    }


    public static void registerDialogRequirement(Enum key, AbstractDialogRequirement requirement) {
        dialog_requirement.put(key, requirement);
    }

    public static AbstractDialogRequirement getDialogRequirement(Enum key) {
        try {
            return dialog_requirement.get(key);
        } catch (NullPointerException e) {
            throw new RuntimeException("Incorrect requirement key: " + key);
        }
    }


    public static void registerTargetType(Enum key, AbstractTargetType requirement) {
        target_type.put(key, requirement);
    }

    public static AbstractTargetType getTargetType(Enum key) {
        try {
            return target_type.get(key);
        } catch (NullPointerException e) {
            throw new RuntimeException("Incorrect target type key: " + key);
        }
    }
}