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

    /**
     * 查詢文章摘要（透過 JSON body 傳入時間範圍）
     * POST /api/summary
     */
    @PostMapping("/summary")
    public ResponseEntity<?> getSummary(@RequestBody QueryRequest request) {
        try {
            var top10 = articleService.findTop10Contents(
                    request.getStartTime(),
                    request.getEndTime()
            );
            List<Map<String, String>> summaries = geminiService.summarizeEachArticle(top10);
            return ResponseEntity.ok(summaries);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("處理摘要失敗: " + e.getMessage());
        }
    }
}
