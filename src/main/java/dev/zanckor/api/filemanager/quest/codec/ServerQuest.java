package dev.zanckor.api.filemanager.quest.codec;

import dev.zanckor.api.filemanager.FileAbstract;

import java.util.List;

public class ServerQuest extends FileAbstract {
    private String id;
    private String title;
    private boolean hasTimeLimit;
    private int timeLimitInSeconds;
    private List<QuestGoal> goals;
    private List<Reward> rewards;
    private List<Requirement> requirements;

    public static ServerQuest createQuest(String id, String title, List<QuestGoal> questGoal, boolean hasTimeLimit, int timeLimitInSeconds, List<Requirement> requirements, List<Reward> rewards) {
        ServerQuest questTemplate = new ServerQuest();

        questTemplate.setId(id);
        questTemplate.setTitle(title);
        questTemplate.setHasTimeLimit(hasTimeLimit);
        questTemplate.setTimeLimitInSeconds(timeLimitInSeconds);
        questTemplate.setRequirements(requirements);
        questTemplate.setRewards(rewards);
        questTemplate.setGoalList(questGoal);

        return questTemplate;
    }

    public class QuestGoal {
        private String type;
        private String target;
        private Integer amount;

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
    }

    public class Reward {
        private String type;
        private String tag;
        private Integer amount;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }
    }

    public class Requirement {
        private String type;
        private int requirements_min;
        private int requirements_max;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
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

    public List<QuestGoal> getGoalList() {
        return goals;
    }

    public void setGoalList(List<QuestGoal> goal) {
        this.goals = goal;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public void setRewards(List<Reward> rewards) {
        this.rewards = rewards;
    }

    public List<Requirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<Requirement> requirements) {
        this.requirements = requirements;
    }
}

