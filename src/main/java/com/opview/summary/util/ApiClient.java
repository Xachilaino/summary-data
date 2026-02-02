package com.opview.summary.util;

import com.opview.summary.config.AppProperties;
import com.opview.summary.dto.news.NewsResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
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
        String apiUrl = appProperties.getNewsApiUrl();
        String apiKey = appProperties.getNewsApiKey();
        String query = appProperties.getNewsApiQuery();
        String language = appProperties.getNewsApiLanguage();
        String pageSize = appProperties.getNewsApiPageSize();
        
        // (新增) 讀取排序設定，如果沒設定預設給 publishedAt
        String sortBy = appProperties.getNewsApiSortBy();
        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = "publishedAt";
        }

        // 組合 API URL
        URI uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("q", query)
                .queryParam("from", fromDate)
                .queryParam("sortBy", sortBy) // (修改) 這裡改用變數
                .queryParam("apiKey", apiKey)
                .queryParam("language", language)
                .queryParam("pageSize", pageSize)
                .build()
                .toUri();

        logger.info("發送 NewsAPI 請求: query={}, lang={}, size={}, sort={}", query, language, pageSize, sortBy);

        try {
            return restTemplate.getForObject(uri, NewsResponseDto.class);
        } catch (HttpClientErrorException e) {
            logger.error("HTTP 錯誤: {} - {}", e.getStatusCode(), e.getStatusText());
            return null;
        } catch (Exception e) {
            logger.error("NewsAPI 請求失敗", e);
            return null;
        }
    }
}