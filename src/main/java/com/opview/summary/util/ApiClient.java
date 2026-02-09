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

    /**
     * [升級版] 抓取新聞文章，支援動態參數
     * @param fromDate 開始日期
     * @param pageSize 每頁筆數 (若為 null 則用預設值)
     * @param sortBy 排序方式 (若為 null 則用預設值)
     */
    public NewsResponseDto fetchArticles(String fromDate, Integer pageSize, String sortBy) {
        String apiKey = appProperties.getNewsApiKey();
        
        if (apiKey == null || apiKey.contains("${")) {
            logger.error("重大錯誤：NEWS_API_KEY 未能正確從環境變數載入！");
            return null;
        }

        // 決定使用傳入的參數或設定檔預設值
        String actualPageSize = (pageSize != null) ? String.valueOf(pageSize) : appProperties.getNewsApiPageSize();
        String actualSortBy = (sortBy != null) ? sortBy : appProperties.getNewsApiSortBy();
        
        // 注意：NewsAPI 的 /everything 端點一定要有 query (q)。
        // 這裡我們維持使用設定檔的 query (例如 "AI"), 但改用 sortBy=popularity 來抓取該主題下的熱門新聞。
        URI uri = UriComponentsBuilder.fromHttpUrl(appProperties.getNewsApiUrl())
                .queryParam("q", appProperties.getNewsApiQuery())
                .queryParam("from", fromDate)
                .queryParam("language", appProperties.getNewsApiLanguage())
                .queryParam("sortBy", actualSortBy)      // 使用動態參數
                .queryParam("pageSize", actualPageSize)  // 使用動態參數
                .queryParam("apiKey", apiKey)
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Java/17");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        logger.info("發送 API 請求: Date={}, Sort={}, Size={}", fromDate, actualSortBy, actualPageSize);

        try {
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

    // [相容舊版] 讓其他程式碼不用改，預設帶入 null
    public NewsResponseDto fetchArticles(String fromDate) {
        return fetchArticles(fromDate, null, null);
    }
}