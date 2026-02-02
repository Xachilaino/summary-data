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
import java.util.stream.Collectors;

@Service
public class GeminiService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);

    // === 定義常數以解決 SonarLint 重複字串警告 ===
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_SUMMARY = "summary";
    private static final String KEY_LIMIT_INFO = "limitInfo";
    private static final String MSG_LIMIT_EXCEEDED = "超出 API 使用上限";

    private final Client client;
    private final Bucket apiCallBucket;
    private final Bucket tokenBucket;

    public GeminiService(Bucket apiCallBucket, Bucket tokenBucket, @Value("${google.api.key}") String apiKey) {
        // [修正點] 直接使用 builder build 出來的 client，移除外層錯誤的 new Client(...)
        this.client = Client.builder().apiKey(apiKey).build();
        this.apiCallBucket = apiCallBucket;
        this.tokenBucket = tokenBucket;
    }

    /**
     * 為文章列表產生摘要 (使用 Title + Description)
     */
    public List<Map<String, String>> summarizeEachArticle(List<Map<String, String>> articles) {
        if (articles == null || articles.isEmpty()) {
            return List.of(Map.of("error", "查無文章內容，無法產生摘要。"));
        }

        return articles.stream().map(article -> {
            String id = article.get(KEY_ID);
            String title = article.get(KEY_TITLE);
            // 改為讀取 description，若沒有則用空字串
            String description = article.getOrDefault(KEY_DESCRIPTION, "");

            // 1. API 頻率限制檢查
            if (!apiCallBucket.tryConsume(1)) {
                return Map.of(
                        KEY_ID, id,
                        KEY_TITLE, title,
                        KEY_SUMMARY, MSG_LIMIT_EXCEEDED,
                        KEY_LIMIT_INFO, "API 呼叫數限制"
                );
            }

            // 2. Token 消耗估算 (標題+簡介)
            int estimatedTokens = Math.max(1, title.length() + description.length());
            if (!tokenBucket.tryConsume(estimatedTokens)) {
                return Map.of(
                        KEY_ID, id,
                        KEY_TITLE, title,
                        KEY_SUMMARY, MSG_LIMIT_EXCEEDED,
                        KEY_LIMIT_INFO, "Token 使用量限制"
                );
            }

            // 3. 準備 Prompt
            String prompt = String.format(
                "請擔任專業新聞編輯。根據以下【標題】與【簡介】，撰寫一段約 50-80 字的中文摘要，幫助讀者快速掌握重點：\n\n【標題】：%s\n【簡介】：%s",
                title, description
            );

            try {
                // 呼叫 Gemini
                GenerateContentResponse response = client.models.generateContent(
                        "gemini-2.5-flash",
                        prompt,
                        null
                );

                return Map.of(
                        KEY_ID, id,
                        KEY_TITLE, title,
                        KEY_DESCRIPTION, description,
                        KEY_SUMMARY, response.text(),
                        KEY_LIMIT_INFO, "OK"
                );

            } catch (Exception e) {
                logger.error("Gemini 產生摘要失敗 ID: {}", id, e);
                return Map.of(
                        KEY_ID, id,
                        KEY_TITLE, title,
                        KEY_SUMMARY, "產生摘要失敗: " + e.getMessage(),
                        KEY_LIMIT_INFO, "ERROR"
                );
            }
        }).collect(Collectors.toList());
    }
}