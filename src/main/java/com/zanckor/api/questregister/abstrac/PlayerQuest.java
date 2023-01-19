package com.zanckor.api.questregister.abstrac;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerQuest {
    public int id;
    public String title;
    public String quest_type;
    public List<String> quest_target;
    public List<Integer> target_quantity;
    public List<Integer> target_current_quantity;
    public boolean hasTimeLimit;
    public int timeLimitInSeconds;
    public boolean completed;

    public static PlayerQuest createQuest(QuestTemplate abstractQuest) {
        PlayerQuest playerQuest = new PlayerQuest();

        playerQuest.setId(abstractQuest.getId());
        playerQuest.setTitle(abstractQuest.getTitle());
        playerQuest.setQuest_type(abstractQuest.getQuest_type());

        playerQuest.setQuest_target(new ArrayList<>(abstractQuest.getQuest_target()));
        playerQuest.setTarget_quantity(new ArrayList<>(abstractQuest.getTarget_quantity()));
        playerQuest.setTarget_current_quantity(new ArrayList<>(Collections.nCopies(abstractQuest.getTarget_quantity().size(), 0)));

        playerQuest.setHasTimeLimit(abstractQuest.isHasTimeLimit());
        playerQuest.setTimeLimitInSeconds(abstractQuest.getTimeLimitInSeconds());

        playerQuest.setCompleted(false);

        return playerQuest;
    }


    public static PlayerQuest incrementProgress(PlayerQuest abstractQuest, int position) {
        PlayerQuest playerQuest = new PlayerQuest();

        playerQuest.setId(abstractQuest.getId());
        playerQuest.setTitle(abstractQuest.getTitle());
        playerQuest.setQuest_type(abstractQuest.getQuest_type());
        playerQuest.setQuest_target(new ArrayList<>(abstractQuest.getQuest_target()));
        playerQuest.setTarget_quantity(new ArrayList<>(abstractQuest.getTarget_quantity()));

        playerQuest.setTarget_current_quantity(new ArrayList<>(abstractQuest.getTarget_current_quantity()));
        playerQuest.increaseTarget_current_quantity(abstractQuest.target_current_quantity, position);

        playerQuest.setHasTimeLimit(abstractQuest.isHasTimeLimit());
        playerQuest.setTimeLimitInSeconds(abstractQuest.getTimeLimitInSeconds());

        playerQuest.setCompleted(false);

        for (int i = 0; i < playerQuest.getQuest_target().size(); i++) {
            if(i == playerQuest.getQuest_target().size()) playerQuest.setCompleted(true);

            if (!(playerQuest.getTarget_current_quantity().get(i) >= playerQuest.getTarget_quantity().get(i))) {
                break;
            }
        }

        return playerQuest;
    }


    public static PlayerQuest incrementProgress(PlayerQuest abstractQuest, int position, int times) {
        PlayerQuest playerQuest = new PlayerQuest();

        playerQuest.setId(abstractQuest.getId());
        playerQuest.setTitle(abstractQuest.getTitle());
        playerQuest.setQuest_type(abstractQuest.getQuest_type());
        playerQuest.setQuest_target(new ArrayList<>(abstractQuest.getQuest_target()));
        playerQuest.setTarget_quantity(new ArrayList<>(abstractQuest.getTarget_quantity()));

        playerQuest.setTarget_current_quantity(new ArrayList<>(abstractQuest.getTarget_current_quantity()));
        playerQuest.increaseTarget_current_quantity(abstractQuest.target_current_quantity, position, times);

        playerQuest.setHasTimeLimit(abstractQuest.isHasTimeLimit());
        playerQuest.setTimeLimitInSeconds(abstractQuest.getTimeLimitInSeconds());

        playerQuest.setCompleted(false);

        for (int i = 0; i < playerQuest.getQuest_target().size(); i++) {
            if(i == playerQuest.getQuest_target().size()) playerQuest.setCompleted(true);

            if (!(playerQuest.getTarget_current_quantity().get(i) >= playerQuest.getTarget_quantity().get(i))) {
                break;
            }
        }

        return playerQuest;
    }

    public static PlayerQuest setProgress(PlayerQuest abstractQuest, int position, int quantity) {
        PlayerQuest playerQuest = new PlayerQuest();

        playerQuest.setId(abstractQuest.getId());
        playerQuest.setTitle(abstractQuest.getTitle());
        playerQuest.setQuest_type(abstractQuest.getQuest_type());
        playerQuest.setQuest_target(new ArrayList<>(abstractQuest.getQuest_target()));
        playerQuest.setTarget_quantity(new ArrayList<>(abstractQuest.getTarget_quantity()));

        playerQuest.setTarget_current_quantity(new ArrayList<>(abstractQuest.getTarget_current_quantity()));
        playerQuest.setTarget_current_quantity(quantity, position);

        playerQuest.setHasTimeLimit(abstractQuest.isHasTimeLimit());
        playerQuest.setTimeLimitInSeconds(abstractQuest.getTimeLimitInSeconds());

        playerQuest.setCompleted(false);

        for (int i = 0; i < playerQuest.getQuest_target().size(); i++) {
            if(i == playerQuest.getQuest_target().size()) playerQuest.setCompleted(true);

            if (!(playerQuest.getTarget_current_quantity().get(i) >= playerQuest.getTarget_quantity().get(i))) {
                break;
            }
        }

        return playerQuest;
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

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public List<Integer> getTarget_current_quantity() {
        return target_current_quantity;
    }

    public void setTarget_current_quantity(List<Integer> target_current_quantity) {
        this.target_current_quantity = target_current_quantity;
    }

    public void increaseTarget_current_quantity(List<Integer> currentList, int position) {
        this.target_current_quantity.set(position, currentList.get(position) + 1);
    }

    public void increaseTarget_current_quantity(List<Integer> currentList, int position, int times) {
        this.target_current_quantity.set(position, currentList.get(position) + times);
    }

    public void setTarget_current_quantity(int quantity, int position) {
        this.target_current_quantity.set(position, quantity);
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

