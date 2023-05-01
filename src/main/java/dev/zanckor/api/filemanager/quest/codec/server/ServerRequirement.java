package dev.zanckor.api.filemanager.quest.codec.server;


import java.util.List;

public class ServerRequirement {
    private String type;
    private int requirements_min;
    private int requirements_max;
    private Integer additionalIntegerData;
    private Double additionalDoubleData;
    private String additionalStringData;
    private List<?> additionalListData;
    private Object additionalClassData;

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

    public void setAdditionalListData(List<?> additionalListData) {
        this.additionalListData = additionalListData;
    }

    public Object getAdditionalClassData() {
        return additionalClassData;
    }

    public void setAdditionalClassData(Object additionalClassData) {
        this.additionalClassData = additionalClassData;
    }

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