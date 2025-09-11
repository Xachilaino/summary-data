package com.opview.summary.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class ApiClient {

    private static final Logger logger = LoggerFactory.getLogger(ApiClient.class);

    private final AppProperties appProperties;
    private final RestTemplate restTemplate;
    private final Gson gson;

    @Autowired
    public ApiClient(AppProperties appProperties, RestTemplate restTemplate, Gson gson) {
        this.appProperties = appProperties;
        this.restTemplate = restTemplate;
        this.gson = gson;
    }

    /**
     * 向 Summary API 發送 POST 請求，取得指定日期的文章資訊。
     *
     * @param startDate 文章開始日期 (格式: yyyy/MM/dd HH:mm:ss)
     * @param endDate   文章結束日期 (格式: yyyy/MM/dd HH:mm:ss)
     * @return 包含 API 回應資料的 SummaryApiResponse 物件，若失敗則為 null。
     */
    public SummaryApiResponse fetchArticles(String startDate, String endDate) {
        // 1. 組 Request JSON
        Map<String, Object> requestJson = new HashMap<>();

        Map<String, String> userInformation = new HashMap<>();
        userInformation.put("service_account", appProperties.getServiceAccount());
        userInformation.put("user_account", appProperties.getUserAccount());
        userInformation.put("token", appProperties.getToken());

        Map<String, Object> summaryInformation = new HashMap<>();
        summaryInformation.put("search_topic", appProperties.getSearchTopic());
        summaryInformation.put("time_range", String.format("%s~%s", startDate, endDate));

        // 🔹 加入 order_type
        Map<String, String> order = new HashMap<>();
        order.put("field", "post_time");
        order.put("order_type", "des");
        summaryInformation.put("search_order", Collections.singletonList(order));

        summaryInformation.put("search_source", appProperties.getSearchSource());

        requestJson.put("user_information", userInformation);
        requestJson.put("summary_information", summaryInformation);

        // 2. JSON 轉字串
        String jsonPayload = gson.toJson(requestJson);

        // 3. 準備請求體 (x-www-form-urlencoded)
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("txtInput_json", jsonPayload);

        // 4. headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.ALL)); // 允許所有回應格式

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        String apiUrl = appProperties.getApiUrl();
        logger.info("正在向 API 發送請求: {}，參數: txtInput_json={}", apiUrl, jsonPayload);

        try {
            // 5. 先拿 raw String（避免 Content-Type 錯誤導致解析失敗）
            ResponseEntity<String> rawResponse = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            logger.info("HTTP 回應狀態: {}, Content-Type: {}",
                    rawResponse.getStatusCodeValue(),
                    rawResponse.getHeaders().getContentType());

            String body = rawResponse.getBody();
            logger.info("API 原始回應: {}", body);

            // 🔹 格式化 JSON 輸出
            try {
                Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
                String prettyJson = prettyGson.toJson(gson.fromJson(body, Object.class));
                logger.info("API 格式化回應:\n{}", prettyJson);
            } catch (Exception e) {
                logger.warn("API 回應不是合法 JSON，無法格式化：{}", e.getMessage());
            }

            if (rawResponse.getStatusCode().is2xxSuccessful() && body != null) {
                try {
                    // 嘗試轉換成 SummaryApiResponse
                    SummaryApiResponse response = gson.fromJson(body, SummaryApiResponse.class);

                    // 🔹 保留原始 JSON（方便 DataProcessingService 輸出）
                    if (response != null) {
                        response.setRawJson(body);
                    }

                    return response;
                } catch (Exception e) {
                    logger.error("將 API 回應轉換為 SummaryApiResponse 失敗，回應內容可能不是 JSON：", e);
                    return null;
                }
            } else {
                logger.error("API 回應非 2xx 或 body 為空");
                return null;
            }

        } catch (HttpClientErrorException e) {
            logger.error("HTTP 錯誤: {} - {}", e.getStatusCode(), e.getStatusText());
            logger.error("回應體: {}", e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            logger.error("呼叫 API 發生未知錯誤: ", e);
            return null;
        }
    }
}
