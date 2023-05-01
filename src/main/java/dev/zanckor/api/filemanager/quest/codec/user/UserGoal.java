package dev.zanckor.api.filemanager.quest.codec.user;

import dev.zanckor.api.filemanager.quest.codec.server.ServerGoal;

import java.util.List;

public class UserGoal {
    private String type;
    private String target;
    private Integer current_amount;
    private Integer amount;

    private Integer additionalIntegerData;
    private Double additionalDoubleData;
    private String additionalStringData;
    private List<?> additionalListData;
    private Object additionalClassData;


    public static UserGoal createQuestGoal(ServerGoal serverGoal) {
        UserGoal userGoal = new UserGoal();

        userGoal.setType(serverGoal.getType());
        userGoal.setTarget(serverGoal.getTarget());
        userGoal.setCurrentAmount(0);
        userGoal.setAmount(serverGoal.getAmount());

        userGoal.setAdditionalStringData(serverGoal.getAdditionalStringData());
        userGoal.setAdditionalIntegerData(serverGoal.getAdditionalIntegerData());
        userGoal.setAdditionalDoubleData(serverGoal.getAdditionalDoubleData());
        userGoal.setAdditionalListData(serverGoal.getAdditionalListData());
        userGoal.setAdditionalClassData(serverGoal.getAdditionalClassData());

        return userGoal;
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

    public Integer getCurrent_amount() {
        return current_amount;
    }

    public void setCurrent_amount(Integer current_amount) {
        this.current_amount = current_amount;
    }

    public Integer getAdditionalIntegerData() {
        return additionalIntegerData;
    }

    public void setAdditionalIntegerData(Integer additionalIntegerData) {
        this.additionalIntegerData = additionalIntegerData;
    }

    public Double getAdditionalDoubleData() {
        return additionalDoubleData;
    }

    public void setAdditionalDoubleData(Double additionalDoubleData) {
        this.additionalDoubleData = additionalDoubleData;
    }

    public String getAdditionalStringData() {
        return additionalStringData;
    }

    public void setAdditionalStringData(String additionalStringData) {
        this.additionalStringData = additionalStringData;
    }

    public List<?> getAdditionalListData() {
        return additionalListData;
    }

    public Object getAdditionalClassData() {
        return additionalClassData;
    }

    public void setAdditionalClassData(Object additionalClassData) {
        this.additionalClassData = additionalClassData;
    }

    public void setAdditionalListData(List<?> additionalListData) {
        this.additionalListData = additionalListData;
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
