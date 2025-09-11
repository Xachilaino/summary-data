package com.opview.summary.entity;

public class ResponseInfo {

    private String search_id;
    private String version;
    private String errorCode;
    private String errorMessage;
    private float queryTime;
    private int total_mention;

    public String getSearch_id() {
        return search_id;
    }

    public void setSearch_id(String search_id) {
        this.search_id = search_id;
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

    public int getTotal_mention() {
        return total_mention;
    }

    public void setTotal_mention(int total_mention) {
        this.total_mention = total_mention;
    }
}