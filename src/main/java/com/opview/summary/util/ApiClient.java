package com.opview.summary.util;

import com.opview.summary.config.AppProperties;
import com.opview.summary.entity.SummaryApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component // 標記為 Spring Component，讓 Spring 管理這個類別
public class ApiClient {

    private static final Logger logger = LoggerFactory.getLogger(ApiClient.class);

    private final AppProperties appProperties;
    private final RestTemplate restTemplate; // Spring 的 RestTemplate 用於 HTTP 請求

    // 透過建構子注入相依性
    @Autowired
    public ApiClient(AppProperties appProperties, RestTemplate restTemplate) {
        this.appProperties = appProperties;
        this.restTemplate = restTemplate;
    }

    /**
     * 向 Summary API 發送 POST 請求，取得指定日期的文章資訊。
     *
     * @param startDate 文章開始日期 (格式: yyyy-MM-dd)
     * @param endDate   文章結束日期 (格式: yyyy-MM-dd)
     * @return 包含 API 回應資料的 SummaryApiResponse 物件，若失敗則可能為 null 或拋出例外。
     */
    public SummaryApiResponse fetchArticles(String startDate, String endDate) {
        // 1. 準備 API 請求參數
        // 根據 API 文件，請求體是 x-www-form-urlencoded
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("service_account", appProperties.getServiceAccount());
        requestBody.add("user_account", appProperties.getUserAccount());
        requestBody.add("token", appProperties.getToken());
        requestBody.add("search_topic", appProperties.getSearchTopic());
        requestBody.add("search_source", appProperties.getSearchSource());
        requestBody.add("start_date", startDate);
        requestBody.add("end_date", endDate);

        // 2. 設定請求標頭 (Headers)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // 設定 Content-Type

        // 3. 建立 HTTP 請求實體
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        String apiUrl = appProperties.getApiUrl();
        logger.info("正在向 API 發送請求: {}，參數: service_account={}, user_account={}, search_topic={}, start_date={}, end_date={}",
                    apiUrl,
                    appProperties.getServiceAccount(),
                    appProperties.getUserAccount(),
                    appProperties.getSearchTopic(),
                    startDate,
                    endDate);

        // 4. 發送 POST 請求並處理回應
        try {
            ResponseEntity<SummaryApiResponse> responseEntity = restTemplate.exchange(
                    apiUrl,                 // API URL
                    HttpMethod.POST,          // HTTP 方法
                    requestEntity,          // 請求實體 (包含 body 和 headers)
                    SummaryApiResponse.class  // 指定期望的回應類型
            );

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                SummaryApiResponse apiResponse = responseEntity.getBody();
                logger.info("API 請求成功，收到回應。");
                // 可以在這裡進一步檢查 apiResponse.getResponseInfo().getErrorCode()
                return apiResponse;
            } else {
                logger.error("API 請求失敗，狀態碼: {}", responseEntity.getStatusCode());
                return null;
            }
        } catch (HttpClientErrorException e) {
            // 處理 HTTP 客戶端錯誤 (例如 4xx 錯誤)
            logger.error("HTTP 客戶端錯誤，請求 API 失敗: {} - {}", e.getStatusCode(), e.getStatusText());
            logger.error("回應體: {}", e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            // 處理其他可能的例外 (例如網路問題、解析錯誤等)
            logger.error("請求 API 時發生未知錯誤: ", e);
            return null;
        }
    }
}