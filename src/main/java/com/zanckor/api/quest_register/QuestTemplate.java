package com.zanckor.api.quest_register;

import java.util.List;

public class QuestTemplate {
    public int id;
    public String title;

    public String quest_type;
    public List<String> quest_target;
    public List<Integer> target_quantity;

    public boolean hasTimeLimit;
    public int timeLimitInSeconds;

    public String requirements_type;
    public int requirements_min;
    public int requirements_max;

    public String reward_type;
    public List<String> reward;
    public List<Integer> reward_quantity;


    public static QuestTemplate createQuest(
            int id, String title,
            Enum quest_type,
            List<String> quest_target, List<Integer> target_quantity,
            boolean hasTimeLimit, int timeLimitInSeconds,
            Enum requirements_type, int requirements_min, int requirements_max,
            Enum reward_type, List<String> reward, List<Integer> reward_quantity){

        QuestTemplate questTemplate = new QuestTemplate();

        questTemplate.setId(id);
        questTemplate.setTitle(title);
        questTemplate.setQuest_type(quest_type.name());
        questTemplate.setQuest_target(quest_target);
        questTemplate.setTarget_quantity(target_quantity);
        questTemplate.setHasTimeLimit(hasTimeLimit);
        questTemplate.setTimeLimitInSeconds(timeLimitInSeconds);
        questTemplate.setRequirements_type(requirements_type.name());
        questTemplate.setRequirements_min(requirements_min);
        questTemplate.setRequirements_max(requirements_max);
        questTemplate.setReward_type(reward_type.name());
        questTemplate.setReward(reward);
        questTemplate.setReward_quantity(reward_quantity);

        return questTemplate;
    }




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuest_type() {
        return quest_type;
    }

    public void setQuest_type(String quest_type) {
        this.quest_type = quest_type;
    }

    public List<String> getQuest_target() {
        return quest_target;
    }

    public void setQuest_target(List<String> quest_target) {
        this.quest_target = quest_target;
    }

    public List<Integer> getTarget_quantity() {
        return target_quantity;
    }

    public void setTarget_quantity(List<Integer> target_quantity) {
        this.target_quantity = target_quantity;
    }

    public String getRequirements_type() {
        return requirements_type;
    }

    public void setRequirements_type(String requirements_type) {
        this.requirements_type = requirements_type;
    }

    public int getRequirements_min() {
        return requirements_min;
    }

    public void setRequirements_min(int requirements_min) {
        this.requirements_min = requirements_min;
    }

    public int getRequirements_max() {
        return requirements_max;
    }

    public void setRequirements_max(int requirements_max) {
        this.requirements_max = requirements_max;
    }

    public String getReward_type() {
        return reward_type;
    }

    public void setReward_type(String reward_type) {
        this.reward_type = reward_type;
    }

    public List<String> getReward() {
        return reward;
    }

    public void setReward(List<String> reward) {
        this.reward = reward;
    }

    public List<Integer> getReward_quantity() {
        return reward_quantity;
    }

    public void setReward_quantity(List<Integer> reward_quantity) {
        this.reward_quantity = reward_quantity;
    }

    public boolean isHasTimeLimit() {
        return hasTimeLimit;
    }

    public void setHasTimeLimit(boolean hasTimeLimit) {
        this.hasTimeLimit = hasTimeLimit;
    }

    public int getTimeLimitInSeconds() {
        return timeLimitInSeconds;
    }

    public void setTimeLimitInSeconds(int timeLimitInSeconds) {
        this.timeLimitInSeconds = timeLimitInSeconds;
    }
}

