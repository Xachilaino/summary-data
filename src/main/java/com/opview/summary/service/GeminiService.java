package com.opview.summary.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final Client client;

    public GeminiService() {
        // 自動讀取 GEMINI_API_KEY
        this.client = new Client();
    }

    /**
     * 逐篇產生文章摘要
     */
    public List<Map<String, String>> summarizeEachArticle(List<Map<String, String>> articles) {
        List<Map<String, String>> summaries = new ArrayList<>();

        for (Map<String, String> article : articles) {
            String id = article.get("id");
            String content = article.get("content");

            if (content == null || content.isBlank()) {
                summaries.add(Map.of(
                        "id", id,
                        "summary", "內容為空，無法產生摘要"
                ));
                continue;
            }

            String prompt = "請針對以下文章內容產生簡短摘要：\n\n" + content;

            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash",
                    prompt,
                    null
            );

            String summary = response.text();

            summaries.add(Map.of(
                    "id", id,
                    "summary", summary
            ));
        }

        return summaries;
    }
}
