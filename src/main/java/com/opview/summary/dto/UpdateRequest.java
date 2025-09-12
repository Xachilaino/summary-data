package com.opview.summary.dto;

import java.util.Map;

public class UpdateRequest {
    private String id; // 文章 ID
    private Map<String, Object> fields; // 欲更新的欄位，例如 {"title":"新標題","view_count":100}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }
}
