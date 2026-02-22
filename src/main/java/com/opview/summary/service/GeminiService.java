package com.opview.summary.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import io.github.bucket4j.Bucket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);
    private final Client client;
    private final Bucket apiCallBucket;
    private final Bucket tokenBucket;

    public GeminiService(Bucket apiCallBucket, Bucket tokenBucket, @Value("${google.api.key}") String apiKey) {
        this.client = Client.builder().apiKey(apiKey).build();
        this.apiCallBucket = apiCallBucket;
        this.tokenBucket = tokenBucket;
    }

    /**
     * 批次摘要：將多篇文章合併為一個 Prompt，產生一篇總評
     * @return 總結文字 (String)
     */
    public String generateOverallSummary(List<Map<String, String>> articles) {
        if (articles == null || articles.isEmpty()) {
            return "查無文章內容，無法產生摘要。";
        }

        // 1. 檢查 API 限制 (只消耗 1 次)
        if (!apiCallBucket.tryConsume(1)) {
            logger.warn("Gemini API 頻率限制 (Batch Mode)");
            return "系統忙碌中，請稍後再試 (Rate Limit Reached)";
        }

        // 2. 組合 Prompt
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("你是一位專業的新聞編輯。請閱讀以下新聞標題與簡介，撰寫一篇約 300~500 字的『重點新聞總評』。\n");
        promptBuilder.append("請整合相似的新聞事件，並條列出今天的關鍵趨勢。\n\n");
        promptBuilder.append("=== 新聞列表 ===\n");

        for (Map<String, String> article : articles) {
            String title = article.get("title");
            String desc = article.getOrDefault("description", "無簡介");
            promptBuilder.append(String.format("- [標題]: %s\n  [簡介]: %s\n\n", title, desc));
        }

        // 3. 呼叫 Gemini
        try {
            // 簡單估算 Token (避免超長)
            if (!tokenBucket.tryConsume(promptBuilder.length() / 2)) {
                return "Token 額度不足，無法處理此批次請求。";
            }

            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash", // 確認使用 1.5-flash
                    promptBuilder.toString(),
                    null
            );

            return response.text();

        } catch (Exception e) {
            logger.error("Gemini 批次摘要失敗", e);
            return "產生摘要失敗: " + e.getMessage();
        }
    }
}