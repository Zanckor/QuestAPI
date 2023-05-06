package dev.zanckor.example.common.enumregistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnumRegistry {
    static List<Class> enumDialogOption = new ArrayList<>();
    static List<Class> enumDialogRequirement = new ArrayList<>();

    static List<Class> enumQuestRequirement = new ArrayList<>();
    static List<Class> enumQuestReward = new ArrayList<>();
    static List<Class> enumQuestGoal = new ArrayList<>();
    static List<Class> enumTargetType = new ArrayList<>();


    public static void registerDialogOption(Class enumClass) {
        enumDialogOption.add(enumClass);
    }

    public static void registerDialogRequirement(Class enumClass) {
        enumDialogRequirement.add(enumClass);
    }

    public static void registerQuestRequirement(Class enumClass) {
        enumQuestRequirement.add(enumClass);
    }

    public static void registerQuestReward(Class enumClass) {
        enumQuestReward.add(enumClass);
    }

    public static void registerQuestGoal(Class enumClass) {
        enumQuestGoal.add(enumClass);
    }

    public static void registerTargetType(Class enumClass) {
        enumTargetType.add(enumClass);
    }


    public static List<Class> getDialogOption() {
        return enumDialogOption;
    }
    public static List<Class> getDialogRequirement() {
        return enumDialogRequirement;
    }

    public static List<Class> getQuestRequirement() {
        return enumQuestRequirement;
    }

    public static List<Class> getQuestReward() {
        return enumQuestReward;
    }

    public static List<Class> getQuestGoal() {
        return enumQuestGoal;
    }

    public static List<Class> getTargetType() {
        return enumTargetType;
    }


    public static Enum getEnum(String enumString, List<Class> enumRegistry) {
        Enum anEnum;

        for (Class enumClass : enumRegistry) {
            Object[] enumValues = enumClass.getEnumConstants();
            boolean hasCorrectEnum;

            for (Object value : enumValues) {
                hasCorrectEnum = value.toString().equalsIgnoreCase(enumString);

                if(hasCorrectEnum) break;
            }

            anEnum = Enum.valueOf(enumClass, enumString);

            if(anEnum != null) return anEnum;
        }

        return null;
    }
}
