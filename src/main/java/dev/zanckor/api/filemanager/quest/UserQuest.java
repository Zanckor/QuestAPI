package dev.zanckor.api.filemanager.quest;

import dev.zanckor.api.filemanager.FileAbstract;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestType;
import dev.zanckor.api.database.LocateHash;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserQuest extends FileAbstract {
    private String id;
    private String title;
    private String quest_type;
    private List<String> quest_target;
    private List<Integer> target_quantity;
    private List<Integer> target_current_quantity;
    private boolean timeLimit;
    private int timeLimitInSeconds;
    private boolean completed;

    public static UserQuest createQuest(ServerQuest abstractQuest, Path path) {
        UserQuest playerQuest = new UserQuest();

        playerQuest.setId(abstractQuest.getId());
        playerQuest.setTitle(abstractQuest.getTitle());
        playerQuest.setQuest_type(abstractQuest.getQuest_type());

        playerQuest.setQuest_target(new ArrayList<>(abstractQuest.getQuest_target()));
        playerQuest.setTarget_quantity(new ArrayList<>(abstractQuest.getTarget_quantity()));
        playerQuest.setTarget_current_quantity(new ArrayList<>(Collections.nCopies(abstractQuest.getTarget_quantity().size(), 0)));

        playerQuest.setTimeLimit(abstractQuest.isHasTimeLimit());
        playerQuest.setTimeLimitInSeconds(abstractQuest.getTimeLimitInSeconds());

        playerQuest.setCompleted(false);

        LocateHash.registerQuestTypeLocation(EnumQuestType.valueOf(playerQuest.getQuest_type()), path);
        LocateHash.registerQuestByID(playerQuest.getId(), path);

        return playerQuest;
    }


    public static UserQuest incrementProgress(UserQuest abstractQuest, int position) {
        UserQuest playerQuest = new UserQuest();

        playerQuest.setId(abstractQuest.getId());
        playerQuest.setTitle(abstractQuest.getTitle());
        playerQuest.setQuest_type(abstractQuest.getQuest_type());
        playerQuest.setQuest_target(new ArrayList<>(abstractQuest.getQuest_target()));
        playerQuest.setTarget_quantity(new ArrayList<>(abstractQuest.getTarget_quantity()));

        playerQuest.setTarget_current_quantity(new ArrayList<>(abstractQuest.getTarget_current_quantity()));
        playerQuest.increaseTarget_current_quantity(abstractQuest.target_current_quantity, position);

        playerQuest.setTimeLimit(abstractQuest.hasTimeLimit());
        playerQuest.setTimeLimitInSeconds(abstractQuest.getTimeLimitInSeconds());

        playerQuest.setCompleted(false);

        for (int i = 0; i < playerQuest.getQuest_target().size(); i++) {
            if (i == playerQuest.getQuest_target().size()) playerQuest.setCompleted(true);

            if (!(playerQuest.getTarget_current_quantity().get(i) >= playerQuest.getTarget_quantity().get(i))) {
                break;
            }
        }

        return playerQuest;
    }


    public static UserQuest incrementProgress(UserQuest abstractQuest, int position, int times) {
        UserQuest playerQuest = new UserQuest();

        playerQuest.setId(abstractQuest.getId());
        playerQuest.setTitle(abstractQuest.getTitle());
        playerQuest.setQuest_type(abstractQuest.getQuest_type());
        playerQuest.setQuest_target(new ArrayList<>(abstractQuest.getQuest_target()));
        playerQuest.setTarget_quantity(new ArrayList<>(abstractQuest.getTarget_quantity()));

        playerQuest.setTarget_current_quantity(new ArrayList<>(abstractQuest.getTarget_current_quantity()));
        playerQuest.increaseTarget_current_quantity(abstractQuest.target_current_quantity, position, times);

        playerQuest.setTimeLimit(abstractQuest.hasTimeLimit());
        playerQuest.setTimeLimitInSeconds(abstractQuest.getTimeLimitInSeconds());

        playerQuest.setCompleted(false);

        for (int i = 0; i < playerQuest.getQuest_target().size(); i++) {
            if (i == playerQuest.getQuest_target().size()) playerQuest.setCompleted(true);

            if (!(playerQuest.getTarget_current_quantity().get(i) >= playerQuest.getTarget_quantity().get(i))) {
                break;
            }
        }

        return playerQuest;
    }

    public static UserQuest setProgress(UserQuest abstractQuest, int position, int quantity) {
        UserQuest playerQuest = new UserQuest();

        playerQuest.setId(abstractQuest.getId());
        playerQuest.setTitle(abstractQuest.getTitle());
        playerQuest.setQuest_type(abstractQuest.getQuest_type());
        playerQuest.setQuest_target(new ArrayList<>(abstractQuest.getQuest_target()));
        playerQuest.setTarget_quantity(new ArrayList<>(abstractQuest.getTarget_quantity()));

        playerQuest.setTarget_current_quantity(new ArrayList<>(abstractQuest.getTarget_current_quantity()));
        playerQuest.setTarget_current_quantity(quantity, position);

        playerQuest.setTimeLimit(abstractQuest.hasTimeLimit());
        playerQuest.setTimeLimitInSeconds(abstractQuest.getTimeLimitInSeconds());

        playerQuest.setCompleted(false);

        for (int i = 0; i < playerQuest.getQuest_target().size(); i++) {
            if (i == playerQuest.getQuest_target().size()) playerQuest.setCompleted(true);

            if (!(playerQuest.getTarget_current_quantity().get(i) >= playerQuest.getTarget_quantity().get(i))) {
                break;
            }
        }

        return playerQuest;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public boolean hasTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(boolean timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getTimeLimitInSeconds() {
        return timeLimitInSeconds;
    }

    public void setTimeLimitInSeconds(int timeLimitInSeconds) {
        this.timeLimitInSeconds = timeLimitInSeconds;
    }
}

