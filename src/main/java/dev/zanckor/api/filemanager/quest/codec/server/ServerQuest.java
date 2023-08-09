package dev.zanckor.api.filemanager.quest.codec.server;

import dev.zanckor.api.filemanager.FileAbstract;

import java.util.List;

public class ServerQuest extends FileAbstract {
    private String id;
    private String title;
    private boolean hasTimeLimit;
    private int timeLimitInSeconds;
    private List<ServerGoal> goals;
    private List<ServerReward> rewards;
    private List<ServerRequirement> requirements;
    private String description;


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

    public boolean hasTimeLimit() {
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

    public List<ServerGoal> getGoalList() {
        return goals;
    }

    public void setGoalList(List<ServerGoal> goals) {
        this.goals = goals;
    }

    public List<ServerReward> getRewards() {
        return rewards;
    }

    public void setRewards(List<ServerReward> ServerRewards) {
        this.rewards = ServerRewards;
    }

    public List<ServerRequirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<ServerRequirement> ServerRequirements) {
        this.requirements = ServerRequirements;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

