package com.opview.summary.entity;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SummaryApiResponse {

    @SerializedName("response_info")
    private ResponseInfo responseInfo;

    @SerializedName("result")
    private List<Article> result;

    // 🔹 保留 API 原始 JSON
    private transient String rawJson;

    public ResponseInfo getResponseInfo() {
        return responseInfo;
    }

    public void setResponseInfo(ResponseInfo responseInfo) {
        this.responseInfo = responseInfo;
    }

    public List<Article> getResult() {
        return result;
    }

    public void setResult(List<Article> result) {
        this.result = result;
    }

    public String getRawJson() {
        return rawJson;
    }

    public void setRawJson(String rawJson) {
        this.rawJson = rawJson;
    }

    @Override
    public String toString() {
        return rawJson != null ? rawJson : super.toString();
    }

    public static class ResponseInfo {
        @SerializedName("search_id")
        private String searchId;

        private String version;

        @SerializedName("errorCode")
        private String errorCode;

        @SerializedName("errorMessage")
        private String errorMessage;

        private double queryTime;

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

        public double getQueryTime() {
            return queryTime;
        }

        public void setQueryTime(double queryTime) {
            this.queryTime = queryTime;
        }

        public int getTotalMention() {
            return totalMention;
        }

        public void setTotalMention(int totalMention) {
            this.totalMention = totalMention;
        }
    }
}
