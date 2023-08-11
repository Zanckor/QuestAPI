package dev.zanckor.api.filemanager.quest.codec.user;

import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.FileAbstract;
import dev.zanckor.api.filemanager.quest.codec.server.ServerQuest;
import dev.zanckor.example.common.enumregistry.EnumRegistry;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserQuest extends FileAbstract {
    private String id;
    private String title;
    private List<UserGoal> questGoals;
    private boolean timeLimit;
    private int timeLimitInSeconds;
    private boolean completed;
    private String description;


    public static UserQuest createQuest(ServerQuest serverQuest, Path path) {
        UserQuest userQuest = new UserQuest();
        List<UserGoal> questGoalList = new ArrayList<>();
        String questModid = path.getFileName().toString().split(File.separator + ".")[0];

        userQuest.setId(serverQuest.getId());
        userQuest.setTitle(serverQuest.getTitle());
        userQuest.setTimeLimit(serverQuest.hasTimeLimit());
        userQuest.setTimeLimitInSeconds(serverQuest.getTimeLimitInSeconds());
        userQuest.setCompleted(false);
        userQuest.setDescription(serverQuest.getDescription());

        for (int goalsIndex = 0; goalsIndex < serverQuest.getGoalList().size(); goalsIndex++) {
            UserGoal questGoal = UserGoal.createQuestGoal(serverQuest.getGoalList().get(goalsIndex), questModid);
            Enum goalEnum = EnumRegistry.getEnum(questGoal.getType(), EnumRegistry.getQuestGoal());

            questGoalList.add(questGoal);
            LocateHash.registerQuestTypeLocation(goalEnum, path);
        }

        userQuest.setQuestGoals(questGoalList);
        LocateHash.registerQuestByID(userQuest.getId(), path);

        return userQuest;
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

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public List<UserGoal> getQuestGoals() {
        return questGoals;
    }

    public void setQuestGoals(List<UserGoal> questGoals) {
        this.questGoals = questGoals;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

