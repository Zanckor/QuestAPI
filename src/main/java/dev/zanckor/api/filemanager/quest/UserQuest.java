package dev.zanckor.api.filemanager.quest;

import dev.zanckor.api.database.LocateHash;
import dev.zanckor.api.filemanager.FileAbstract;
import dev.zanckor.example.common.enumregistry.enumquest.EnumQuestType;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class UserQuest extends FileAbstract {
    private String id;
    private String title;
    private List<QuestGoal> questGoals;
    private boolean timeLimit;
    private int timeLimitInSeconds;
    private boolean completed;


    public static UserQuest createQuest(ServerQuest serverQuest, Path path) {
        UserQuest userQuest = new UserQuest();
        List<QuestGoal> questGoalList = new ArrayList<>();

        userQuest.setId(serverQuest.getId());
        userQuest.setTitle(serverQuest.getTitle());
        userQuest.setTimeLimit(serverQuest.isHasTimeLimit());
        userQuest.setTimeLimitInSeconds(serverQuest.getTimeLimitInSeconds());
        userQuest.setCompleted(false);

        for (int goalsIndex = 0; goalsIndex < serverQuest.getGoalList().size(); goalsIndex++) {
            QuestGoal questGoal = QuestGoal.createQuestGoal(serverQuest.getGoalList().get(goalsIndex));

            questGoalList.add(questGoal);
            LocateHash.registerQuestTypeLocation(EnumQuestType.valueOf(questGoal.getType()), path);
        }

        userQuest.setQuestGoals(questGoalList);
        LocateHash.registerQuestByID(userQuest.getId(), path);

        return userQuest;
    }

    public static class QuestGoal {
        private String type;
        private String target;
        private Integer current_amount;
        private Integer amount;

        public static QuestGoal createQuestGoal(ServerQuest.QuestGoal serverQuestGoal) {
            UserQuest.QuestGoal userQuestGoal = new QuestGoal();

            userQuestGoal.setType(serverQuestGoal.getType());
            userQuestGoal.setTarget(serverQuestGoal.getTarget());
            userQuestGoal.setCurrentAmount(0);
            userQuestGoal.setAmount(serverQuestGoal.getAmount());

            return userQuestGoal;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }

        public Integer getCurrentAmount() {
            return current_amount;
        }

        public void setCurrentAmount(Integer amount) {
            this.current_amount = amount;
        }

        public void incrementCurrentAmount(Integer amount) {
            this.current_amount += amount;
        }
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

    public List<QuestGoal> getQuestGoals() {
        return questGoals;
    }

    public void setQuestGoals(List<QuestGoal> questGoals) {
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
}

