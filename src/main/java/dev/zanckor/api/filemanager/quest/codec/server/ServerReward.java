package dev.zanckor.api.filemanager.quest.codec.server;

import java.util.List;

public class ServerReward {
    private String type;
    private String tag;
    private Integer amount;
    private Integer additionalIntegerData;
    private Double additionalDoubleData;
    private String additionalStringData;
    private List<?> additionalListData;
    private Object additionalClassData;

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

    public Integer getAdditionalIntegerData() {
        return additionalIntegerData;
    }

    public void setAdditionalIntegerData(Integer additionalIntegerData) {
        this.additionalIntegerData = additionalIntegerData;
    }

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