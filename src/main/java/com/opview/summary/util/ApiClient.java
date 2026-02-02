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

    /**
     * 從 NewsAPI 取得文章
     * @param fromDate 起始日期 (格式: yyyy-MM-dd)
     * @return NewsResponseDto
     */
    public NewsResponseDto fetchArticles(String fromDate) {
        // 從 application.properties 讀取設定
        String apiUrl = appProperties.getNewsApiUrl();
        String apiKey = appProperties.getNewsApiKey();
        String query = appProperties.getNewsApiQuery();

        // 建立請求 URL (例如: https://newsapi.org/v2/everything?q=AI&from=2023-10-01&apiKey=...)
        URI uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("q", query)
                .queryParam("from", fromDate)
                .queryParam("sortBy", "publishedAt")
                .queryParam("apiKey", apiKey)
                .queryParam("language", "en") // 建議先限制英文，Gemini 處理較穩
                .build()
                .toUri();

        logger.info("正在向 NewsAPI 發送請求: query={}, date={}", query, fromDate);

        try {
            // 發送 GET 請求
            NewsResponseDto response = restTemplate.getForObject(uri, NewsResponseDto.class);

            if (response != null && "ok".equals(response.getStatus())) {
                logger.info("成功取得 {} 筆新聞資料", response.getTotalResults());
                return response;
            } else {
                logger.error("API 回應狀態非 OK: {}", response != null ? response.getStatus() : "null");
                return null;
            }

        } catch (HttpClientErrorException e) {
            logger.error("HTTP 錯誤: {} - {}", e.getStatusCode(), e.getStatusText());
            return null;
        } catch (Exception e) {
            logger.error("呼叫 NewsAPI 發生未知錯誤", e);
            return null;
        }
    }
}