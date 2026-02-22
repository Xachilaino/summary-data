package com.opview.summary.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "opview.api")
public class AppProperties {

    // === News API 設定 ===
    @Value("${newsapi.key}")
    private String newsApiKey;

    @Value("${newsapi.url}")
    private String newsApiUrl;

    @Value("${newsapi.query}")
    private String newsApiQuery;

    @Value("${newsapi.language}")
    private String newsApiLanguage;

    @Value("${newsapi.pageSize}")
    private String newsApiPageSize;

    // (新增) 排序設定
    @Value("${newsapi.sortBy}")
    private String newsApiSortBy;

    // === Getters ===
    public String getNewsApiKey() { return newsApiKey; }
    public String getNewsApiUrl() { return newsApiUrl; }
    public String getNewsApiQuery() { return newsApiQuery; }
    public String getNewsApiLanguage() { return newsApiLanguage; }
    public String getNewsApiPageSize() { return newsApiPageSize; }
    public String getNewsApiSortBy() { return newsApiSortBy; } // (新增 Getter)

    // ... 其他舊欄位保持不變 ...
    private String cronExpression;
    public String getCronExpression() { return cronExpression; }
    public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }
}