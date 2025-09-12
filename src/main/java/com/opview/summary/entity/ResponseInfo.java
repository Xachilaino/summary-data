package com.opview.summary.entity;

import com.google.gson.annotations.SerializedName;

public class ResponseInfo {

    @SerializedName("search_id")
    private String searchId;

    private String version;

    @SerializedName("errorCode")
    private String errorCode;

    @SerializedName("errorMessage")
    private String errorMessage;

    private float queryTime;

    @SerializedName("total_mention")
    private int totalMention;

    // === Getter / Setter ===
    public String getSearchId() {
        return searchId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public float getQueryTime() {
        return queryTime;
    }

    public void setQueryTime(float queryTime) {
        this.queryTime = queryTime;
    }

    public int getTotalMention() {
        return totalMention;
    }

    public void setTotalMention(int totalMention) {
        this.totalMention = totalMention;
    }
}
