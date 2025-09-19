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


    // POST /api/summary
    @PostMapping("/summary")
    public ResponseEntity<?> getSummary(@RequestBody QueryRequest request) {
        // 將請求的 JSON 轉換為 QueryRequest
        try {
            var top10 = articleService.findTop10Contents(
                    request.getStartTime(),
                    request.getEndTime()
                    // 呼叫 articleService 的 findTop10Contents 方法
            );
            List<Map<String, String>> summaries = geminiService.summarizeEachArticle(top10);
            return ResponseEntity.ok(summaries);
            // 由 geminiService 負責處理與gemini API相關的邏輯

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("處理摘要失敗: " + e.getMessage());
        }
    }
}
