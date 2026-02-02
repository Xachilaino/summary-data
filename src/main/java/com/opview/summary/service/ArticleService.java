package com.opview.summary.service;

import com.opview.summary.dao.ArticleDao;
import com.opview.summary.dto.DeleteRequest;
import com.opview.summary.dto.QueryRequest;
import com.opview.summary.dto.UpdateRequest;
import com.opview.summary.entity.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleService {

    private final ArticleDao articleDao;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public ArticleService(ArticleDao articleDao, NamedParameterJdbcTemplate jdbcTemplate) {
        this.articleDao = articleDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    // 查詢文章 (對應新的 news_article 表)
    public List<Article> queryArticles(QueryRequest request) {
        // 修改：移除 sentiment_tag 篩選，將 post_time 改為 published_at
        String sql = """
            SELECT * FROM news_article
            WHERE published_at BETWEEN :start AND :end
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("start", request.getStartTime())
                .addValue("end", request.getEndTime());

        return jdbcTemplate.query(sql, params,
                (rs, rowNum) -> {
                    Article a = new Article();
                    // 修改：ID 改為 Long，並對應新欄位名稱
                    a.setId(rs.getLong("id"));
                    a.setSourceName(rs.getString("source_name"));
                    a.setAuthor(rs.getString("author"));
                    a.setTitle(rs.getString("title"));
                    a.setDescription(rs.getString("description"));
                    a.setUrl(rs.getString("url"));
                    a.setUrlToImage(rs.getString("url_to_image"));
                    
                    a.setPublishedAt(rs.getTimestamp("published_at") != null
                            ? rs.getTimestamp("published_at").toLocalDateTime()
                            : null);
                            
                    a.setContent(rs.getString("content"));
                    a.setSummary(rs.getString("summary"));
                    
                    a.setCreateTime(rs.getTimestamp("create_time") != null
                            ? rs.getTimestamp("create_time").toLocalDateTime()
                            : null);
                            
                    a.setUpdateTime(rs.getTimestamp("update_time") != null
                            ? rs.getTimestamp("update_time").toLocalDateTime()
                            : null);
                    return a;
                });
    }

    // 更新文章
    public String updateArticle(UpdateRequest request) {
        if (request.getFields() == null || request.getFields().isEmpty()) {
            return "更新失敗：未提供任何更新欄位";
        }

        // 修改：定義欄位映射 (前端欄位名稱 -> 資料庫欄位名稱)
        // 這樣可以防止 SQL Injection 並且處理駝峰式命名轉底線
        Map<String, String> allowedFields = new HashMap<>();
        allowedFields.put("title", "title");
        allowedFields.put("content", "content");
        allowedFields.put("author", "author");
        allowedFields.put("description", "description");
        allowedFields.put("summary", "summary"); // 新增 summary 欄位
        allowedFields.put("sourceName", "source_name");
        allowedFields.put("urlToImage", "url_to_image");

        StringBuilder sql = new StringBuilder("UPDATE news_article SET ");
        MapSqlParameterSource params = new MapSqlParameterSource();

        boolean hasValidField = false;
        StringBuilder ignoredFields = new StringBuilder();

        for (Map.Entry<String, Object> entry : request.getFields().entrySet()) {
            String inputField = entry.getKey();
            
            // 檢查是否為允許更新的欄位
            if (allowedFields.containsKey(inputField)) {
                String dbColumn = allowedFields.get(inputField);
                sql.append(dbColumn).append(" = :").append(inputField).append(", ");
                params.addValue(inputField, entry.getValue());
                hasValidField = true;
            } else {
                ignoredFields.append(inputField).append(" ");
            }
        }

        if (!hasValidField) {
            return "更新失敗：沒有有效的更新欄位 (忽略: " + ignoredFields + ")";
        }

        // 修改：ID 參數處理 (注意 request.getId() 如果是字串可能需要轉型，這裡假設 request 傳來的是 Long 或數字字串)
        sql.append("update_time = :updateTime WHERE id = :id");
        params.addValue("updateTime", LocalDateTime.now());
        params.addValue("id", request.getId()); 

        int rows = jdbcTemplate.update(sql.toString(), params);

        if (rows > 0) {
            if (ignoredFields.length() > 0) {
                return "部分更新成功，但以下欄位不可更動或不存在已被忽略: " + ignoredFields;
            } else {
                return "更新成功";
            }
        } else {
            return "更新失敗：找不到指定 ID";
        }
    }

    // 刪除文章
    public List<Map<String, Object>> deleteArticlesWithInfo(DeleteRequest request) {
        // 修改：查詢條件改為 news_article 與 published_at
        String selectSql = """
            SELECT id, title FROM news_article
            WHERE published_at BETWEEN :start AND :end
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("start", request.getStartTime())
                .addValue("end", request.getEndTime());

        List<Map<String, Object>> toDelete = jdbcTemplate.query(selectSql, params,
                (rs, rowNum) -> Map.of(
                        "id", rs.getLong("id"), // ID 改為 Long
                        "title", rs.getString("title")
                ));

        if (!toDelete.isEmpty()) {
            // 修改：刪除語句
            String deleteSql = """
                DELETE FROM news_article
                WHERE published_at BETWEEN :start AND :end
            """;
            jdbcTemplate.update(deleteSql, params);
        }

        return toDelete;
    }

    // 查詢前 10 筆文章給 Gemini (回傳 id + title + content)
    public List<Map<String, String>> findTop10Contents(LocalDateTime start, LocalDateTime end) {
        // 修改：使用 news_article，並按 published_at 排序
        String sql = """
            SELECT id, title, content
            FROM news_article
            WHERE published_at BETWEEN :start AND :end
            ORDER BY published_at DESC
            LIMIT 10
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("start", start)
                .addValue("end", end);

        return jdbcTemplate.query(sql, params,
                (rs, rowNum) -> {
                    // 注意：content 可能為 null，處理一下避免 Map.of 報錯
                    String content = rs.getString("content");
                    if (content == null) content = "";
                    
                    return Map.of(
                        "id", String.valueOf(rs.getLong("id")), // 轉字串方便後續處理
                        "title", rs.getString("title"),
                        "content", content
                    );
                });
    }
}