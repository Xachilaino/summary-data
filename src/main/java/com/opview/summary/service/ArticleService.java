package com.opview.summary.service;

import com.opview.summary.dao.ArticleDao;
import com.opview.summary.dto.QueryRequest;
import com.opview.summary.dto.UpdateRequest;
import com.opview.summary.dto.DeleteRequest;
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

    // 查詢文章 (已更新為 news_article)
    public List<Article> queryArticles(QueryRequest request) {
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
                    a.setId(rs.getLong("id")); // ID 改為 Long
                    a.setTitle(rs.getString("title"));
                    a.setDescription(rs.getString("description")); // 新增 description
                    a.setContent(rs.getString("content"));
                    a.setSourceName(rs.getString("source_name")); // 改為 source_name
                    a.setUrl(rs.getString("url"));
                    a.setUrlToImage(rs.getString("url_to_image"));
                    a.setPublishedAt(rs.getTimestamp("published_at") != null
                            ? rs.getTimestamp("published_at").toLocalDateTime()
                            : null);
                    a.setAuthor(rs.getString("author"));
                    a.setSummary(rs.getString("summary")); // 取出 summary
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

        // 定義允許更新的欄位對照表 (前端欄位 -> 資料庫欄位)
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("title", "title");
        fieldMapping.put("content", "content");
        fieldMapping.put("description", "description");
        fieldMapping.put("author", "author");
        fieldMapping.put("sourceName", "source_name");
        fieldMapping.put("summary", "summary");

        StringBuilder sql = new StringBuilder("UPDATE news_article SET ");
        MapSqlParameterSource params = new MapSqlParameterSource();

        boolean hasValidField = false;
        StringBuilder ignoredFields = new StringBuilder();

        for (Map.Entry<String, Object> entry : request.getFields().entrySet()) {
            String inputField = entry.getKey();
            
            if (fieldMapping.containsKey(inputField)) {
                String dbColumn = fieldMapping.get(inputField);
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

        sql.append("update_time = :updateTime WHERE id = :id");
        params.addValue("updateTime", LocalDateTime.now());
        params.addValue("id", request.getId());

        int rows = jdbcTemplate.update(sql.toString(), params);

        return rows > 0 ? "更新成功" : "更新失敗：找不到指定 ID";
    }

    // 刪除文章
    public List<Map<String, Object>> deleteArticlesWithInfo(DeleteRequest request) {
        String selectSql = """
            SELECT id, title FROM news_article
            WHERE published_at BETWEEN :start AND :end
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("start", request.getStartTime())
                .addValue("end", request.getEndTime());

        List<Map<String, Object>> toDelete = jdbcTemplate.query(selectSql, params,
                (rs, rowNum) -> Map.of(
                        "id", rs.getLong("id"), // Long
                        "title", rs.getString("title")
                ));

        if (!toDelete.isEmpty()) {
            String deleteSql = "DELETE FROM news_article WHERE published_at BETWEEN :start AND :end";
            jdbcTemplate.update(deleteSql, params);
        }

        return toDelete;
    }

    // 查詢前 10 筆文章給 Gemini (修改：抓取 description 而非 content)
    public List<Map<String, String>> findTop10Contents(LocalDateTime start, LocalDateTime end) {
        String sql = """
            SELECT id, title, description
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
                    String desc = rs.getString("description");
                    if (desc == null) desc = ""; // 避免 null
                    
                    return Map.of(
                        "id", String.valueOf(rs.getLong("id")),
                        "title", rs.getString("title"),
                        "description", desc // 傳回 description
                    );
                });
    }
}