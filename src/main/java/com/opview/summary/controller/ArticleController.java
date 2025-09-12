package com.opview.summary.controller;

import com.opview.summary.dto.QueryRequest;
import com.opview.summary.dto.UpdateRequest;
import com.opview.summary.dto.DeleteRequest;
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
     * POST /api/articles/query
     */
    @PostMapping("/query")
    public ResponseEntity<?> queryArticles(@RequestBody QueryRequest request) {
        List<Article> result = articleService.queryArticles(request);
        if (result == null || result.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "查無符合條件的文章"
            ));
        }
        return ResponseEntity.ok(Map.of(
                "success", true,
                "articles", result
        ));
    }

    /**
     * 更新文章
     * POST /api/articles/update
     */
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateArticle(@RequestBody UpdateRequest request) {
        String message = articleService.updateArticle(request);

        boolean success = message.contains("成功"); // 判斷是否成功

        return ResponseEntity.ok(Map.of(
                "success", success,
                "id", request.getId(),
                "message", message
        ));
    }

    /**
     * 刪除文章
     * POST /api/articles/delete
     */
    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteArticles(@RequestBody DeleteRequest request) {
        List<Map<String, Object>> deleted = articleService.deleteArticlesWithInfo(request);

        if (deleted.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "查無符合條件的文章可刪除"
            ));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "deletedCount", deleted.size(),
                "deletedArticles", deleted
        ));
    }
}
