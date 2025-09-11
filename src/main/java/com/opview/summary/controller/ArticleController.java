package com.opview.summary.controller;

import com.opview.summary.entity.Article;
import com.opview.summary.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    /**
     * 查詢文章
     */
    @PostMapping("/query")
    public ResponseEntity<?> queryArticles(@RequestBody Map<String, String> request) {
        String startTime = request.get("startTime");
        String endTime = request.get("endTime");
        List<Article> result = articleService.queryArticles(startTime, endTime);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", result
        ));
    }

    /**
     * 更新文章
     */
    @PostMapping("/update")
    public ResponseEntity<?> updateArticle(@RequestBody Map<String, Object> request) {
        String id = (String) request.get("id");
        Map<String, Object> fields = (Map<String, Object>) request.get("fields");

        boolean success = articleService.updateArticle(id, fields);

        return ResponseEntity.ok(Map.of(
                "success", success,
                "message", success ? "更新成功" : "找不到文章",
                "updatedId", id
        ));
    }

    /**
     * 刪除文章
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deleteArticles(@RequestBody Map<String, String> request) {
        String startTime = request.get("startTime");
        String endTime = request.get("endTime");
        int deleted = articleService.deleteArticles(startTime, endTime);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "deletedCount", deleted
        ));
    }
}
