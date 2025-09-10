package com.opview.summary.util;

import com.google.gson.Gson;
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
        // 1. 建立符合 API 文件要求的 Request JSON 物件
        Map<String, Object> requestJson = new HashMap<>();

        Map<String, String> userInformation = new HashMap<>();
        userInformation.put("service_account", appProperties.getServiceAccount());
        userInformation.put("user_account", appProperties.getUserAccount());
        userInformation.put("token", appProperties.getToken());

        Map<String, Object> summaryInformation = new HashMap<>();
        summaryInformation.put("search_topic", appProperties.getSearchTopic());
        summaryInformation.put("time_range", String.format("%s~%s", startDate, endDate));
        summaryInformation.put("search_source", appProperties.getSearchSource());
        summaryInformation.put("search_order", Collections.singletonList(
                Collections.singletonMap("field", "post_time")
        ));
        
        requestJson.put("user_information", userInformation);
        requestJson.put("summary_information", summaryInformation);
        
        // 2. 將 JSON 物件轉換為字串
        String jsonPayload = gson.toJson(requestJson);

        // 3. 準備 x-www-form-urlencoded 格式的請求體
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("txtInput_json", jsonPayload);

        // 4. 設定請求標頭
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON)); // 告知伺服器我們期望 JSON 回應

        // 5. 建立 HTTP 請求實體
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        String apiUrl = appProperties.getApiUrl();
        logger.info("正在向 API 發送請求: {}，參數: txtInput_json={}", apiUrl, jsonPayload);

        // 6. 發送 POST 請求並處理回應
        try {
            ResponseEntity<SummaryApiResponse> responseEntity = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    SummaryApiResponse.class
            );

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                SummaryApiResponse apiResponse = responseEntity.getBody();
                if (apiResponse != null) {
                    logger.info("API 請求成功，收到回應。");
                    return apiResponse;
                } else {
                    logger.error("API 回應體為空。");
                    return null;
                }
            } else {
                logger.error("API 請求失敗，狀態碼: {}", responseEntity.getStatusCode());
                return null;
            }
        } catch (HttpClientErrorException e) {
            logger.error("HTTP 客戶端錯誤，請求 API 失敗: {} - {}", e.getStatusCode(), e.getStatusText());
            logger.error("回應體: {}", e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            logger.error("請求 API 時發生未知錯誤: ", e);
            return null;
        }
    }
}