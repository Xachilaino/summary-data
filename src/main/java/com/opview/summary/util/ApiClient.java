package com.opview.summary.util;

import com.opview.summary.config.AppProperties;
import com.opview.summary.dto.news.NewsResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class ApiClient {
    private static final Logger logger = LoggerFactory.getLogger(ApiClient.class);
    private final AppProperties appProperties;
    private final RestTemplate restTemplate;

    @Autowired
    public ApiClient(AppProperties appProperties, RestTemplate restTemplate) {
        this.appProperties = appProperties;
        this.restTemplate = restTemplate;
    }

    public NewsResponseDto fetchArticles(String fromDate) {
        String apiKey = appProperties.getNewsApiKey();
        
        // 1. 防止 Placeholder 未解析導致 400
        if (apiKey == null || apiKey.contains("${")) {
            logger.error("重大錯誤：NEWS_API_KEY 未能正確從環境變數載入！目前值: {}", apiKey);
            return null;
        }

        // 2. 建立 URI
        URI uri = UriComponentsBuilder.fromHttpUrl(appProperties.getNewsApiUrl())
                .queryParam("q", appProperties.getNewsApiQuery())
                .queryParam("from", fromDate)
                .queryParam("language", appProperties.getNewsApiLanguage())
                .queryParam("sortBy", appProperties.getNewsApiSortBy())
                .queryParam("pageSize", appProperties.getNewsApiPageSize())
                .queryParam("apiKey", apiKey)
                .build()
                .toUri();

        // 3. 【核心修正】加入 User-Agent，這是 NewsAPI 要求的
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Java/17");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        logger.info("正在發送請求至: {}", uri.getScheme() + "://" + uri.getHost() + uri.getPath() + "?q=...");

        try {
            // 使用 exchange 才能帶入 headers
            ResponseEntity<NewsResponseDto> response = restTemplate.exchange(
                    uri, 
                    HttpMethod.GET, 
                    entity, 
                    NewsResponseDto.class
            );
            return response.getBody();
        } catch (Exception e) {
            logger.error("NewsAPI 請求失敗: {}", e.getMessage());
            return null;
        }
    }
}