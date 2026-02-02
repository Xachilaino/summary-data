package com.opview.summary.controller;

import com.opview.summary.dto.QueryRequest;
import com.opview.summary.service.ArticleService;
import com.opview.summary.service.GeminiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SummaryController {

    private final ArticleService articleService;
    private final GeminiService geminiService;

    public SummaryController(ArticleService articleService, GeminiService geminiService) {
        this.articleService = articleService;
        this.geminiService = geminiService;
    }

    @PostMapping("/summary")
    public ResponseEntity<?> getSummary(@RequestBody QueryRequest request) {
        try {
            // 1. 撈取前 10 篇文章
            var top10Articles = articleService.findTop10Contents(
                    request.getStartTime(),
                    request.getEndTime()
            );

            // 2. 呼叫 Gemini 產生一篇總評
            String overallSummaryText = geminiService.generateOverallSummary(top10Articles);

            // 3. 回傳組合結果 (Map)
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "overallSummary", overallSummaryText,
                    "articles", top10Articles
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("處理摘要失敗: " + e.getMessage());
        }
    }
}