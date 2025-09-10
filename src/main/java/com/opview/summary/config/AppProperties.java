package com.opview.summary.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration // 註冊為Spring組件，讓Spring管理
@ConfigurationProperties(prefix = "opview.api") // 指定屬性前綴，例如在 application.properties 中為 opview.api
public class AppProperties {

    // API憑證相關資訊，來自 "Summary API使用資訊-ps_trial_001.txt"
    private String serviceAccount;
    private String userAccount;
    private String token;
    private String searchTopic;
    private String searchSource;

    // API請求URL
    private String apiUrl;

    // 排程設定
    private String cronExpression;
    
    // 省略 Getter 和 Setter
    // 透過 IDE 自動產生即可

    public String getServiceAccount() {
        return serviceAccount;
    }

    public void setServiceAccount(String serviceAccount) {
        this.serviceAccount = serviceAccount;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSearchTopic() {
        return searchTopic;
    }

    public void setSearchTopic(String searchTopic) {
        this.searchTopic = searchTopic;
    }

    public String getSearchSource() {
        return searchSource;
    }

    public void setSearchSource(String searchSource) {
        this.searchSource = searchSource;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
    
    public String getCronExpression() {
        return cronExpression;
    }
    
    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }
}