package com.opview.summary.entity;

import java.util.List;

public class SummaryApiResponse {

    private ResponseInfo responseInfo;
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
        private String searchId;
        private String version;
        private String errorCode;
        private String errorMessage;
        private double queryTime;
        private int totalMention;

        // Getter / Setter ...
        
        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }
}
