package com.opview.summary.entity;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SummaryApiResponse {

    @SerializedName("response_info")
    private ResponseInfo responseInfo;

    @SerializedName("result")
    private List<Article> result;

    // 🔹 保留 API 原始 JSON（方便 debug）
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
}
