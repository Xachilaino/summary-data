package com.opview.summary.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GeminiService {

    private final Client client;
    private final Bucket apiCallBucket;
    private final Bucket tokenBucket;

    public GeminiService(Bucket apiCallBucket, Bucket tokenBucket) {
        this.client = new Client(); // 會自動讀取 GEMINI_API_KEY
        this.apiCallBucket = apiCallBucket;
        this.tokenBucket = tokenBucket;
    }

    /**
     * 逐篇產生文章摘要 (回傳包含 id + title + content + summary + limitInfo)
     */
    public List<Map<String, String>> summarizeEachArticle(List<Map<String, String>> articles) {
        if (articles == null || articles.isEmpty()) {
            return List.of(Map.of("error", "查無文章內容，無法產生摘要。"));
        }

        return articles.stream().map(article -> {
            String id = article.get("id");
            String title = article.get("title");
            String content = article.get("content");

            // === API 呼叫數限制 ===
            if (!apiCallBucket.tryConsume(1)) {
                return Map.of(
                        "id", id,
                        "title", title,
                        "content", content,
                        "summary", "超出 API 使用上限",
                        "limitInfo", "API 呼叫數限制"
                );
            }

            // === Token 限制 ===
            int estimatedTokens = estimateTokens(content);
            if (!tokenBucket.tryConsume(estimatedTokens)) {
                return Map.of(
                        "id", id,
                        "title", title,
                        "content", content,
                        "summary", "超出 API 使用上限",
                        "limitInfo", "Token 使用量限制",
                        "estimatedTokens", String.valueOf(estimatedTokens)
                );
            }

            // === 呼叫 Gemini API ===
            String prompt = "請針對以下文章內容做摘要，簡短描述重點：\n\n" + content;

            try {
                GenerateContentResponse response = client.models.generateContent(
                        "gemini-2.5-flash",
                        prompt,
                        null
                );

                return Map.of(
                        "id", id,
                        "title", title,
                        "content", content,
                        "summary", response.text(),
                        "limitInfo", "OK",
                        "estimatedTokens", String.valueOf(estimatedTokens)
                );

            } catch (Exception e) {
                return Map.of(
                        "id", id,
                        "title", title,
                        "content", content,
                        "summary", "產生摘要失敗: " + e.getMessage(),
                        "limitInfo", "ERROR"
                );
            }
        }).collect(Collectors.toList());
    }


    private int estimateTokens(String text) {
        if (text == null) return 0;
        return Math.max(1, text.length());
        // 假設 Token = 字數
    }
}
