package com.zanckor.questapi.createQuest.kill;

import com.zanckor.questapi.createQuest.ServerQuest;

import java.sql.Array;
import java.util.List;

public class ClientKillQuest {
    public int id;
    public String title;

    public String quest_type;
    public List<String> target_entity;
    public List<Integer> target_quantity;
    public List<Integer> target_current_quantity;
    public boolean completed;

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

    public List<String> getTarget_entity() {
        return target_entity;
    }

    public void setTarget_entity(List<String> target_entity) {
        this.target_entity = target_entity;
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

    public void setTarget_current_quantity(int target_current_quantity, int position) {
        this.target_current_quantity.set(position, target_current_quantity);
    }

    public void increaseTarget_current_quantity(int currentValue, int position) {
        this.target_current_quantity.set(position, currentValue + 1);
    }



    public static ClientKillQuest createQuest(ServerQuest abstractQuest){
        ClientKillQuest playerQuest = new ClientKillQuest();

        playerQuest.setId(abstractQuest.getId());
        playerQuest.setTitle(abstractQuest.getTitle());
        playerQuest.setQuest_type(abstractQuest.getQuest_type());
        playerQuest.setTarget_entity(abstractQuest.getTarget_entity());
        playerQuest.setTarget_quantity(abstractQuest.getTarget_quantity());

        for(int i = 0; i < playerQuest.getTarget_entity().size() - 1; i++) {
            playerQuest.setTarget_current_quantity(0, i);
        }

        playerQuest.setCompleted(false);

        return playerQuest;
    }


    public static ClientKillQuest incrementProgress(ClientKillQuest abstractQuest, int position){
        ClientKillQuest playerQuest = new ClientKillQuest();

        playerQuest.setId(abstractQuest.getId());
        playerQuest.setTitle(abstractQuest.getTitle());
        playerQuest.setQuest_type(abstractQuest.getQuest_type());
        playerQuest.setTarget_entity(abstractQuest.getTarget_entity());
        playerQuest.setTarget_quantity(abstractQuest.getTarget_quantity());


        playerQuest.increaseTarget_current_quantity(abstractQuest.getTarget_current_quantity().get(position), position);

        playerQuest.setCompleted(false);

        for(int i = 0; i < playerQuest.getTarget_entity().size() - 1; i++) {
            if (playerQuest.getTarget_current_quantity().get(i) >= playerQuest.target_quantity.get(i)) {
                playerQuest.setCompleted(true);
            } else {
                break;
            }
        }

        return playerQuest;
    }
}

