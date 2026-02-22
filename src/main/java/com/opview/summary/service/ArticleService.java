package com.opview.summary.service;

import com.opview.summary.dto.DeleteRequest;
import com.opview.summary.dto.QueryRequest;
import com.opview.summary.dto.UpdateRequest;
import com.opview.summary.entity.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@Service
public class ArticleService {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public ArticleService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 既有的方法 (SummaryController 使用)
    public List<Map<String, String>> findTop10Contents(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT id, title, description FROM news_article " +
                     "WHERE published_at BETWEEN :start AND :end ORDER BY published_at DESC LIMIT 10";
        
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("start", start)
                .addValue("end", end);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> Map.of(
                "id", String.valueOf(rs.getLong("id")),
                "title", rs.getString("title"),
                "description", rs.getString("description") != null ? rs.getString("description") : ""
        ));
    }

    // [新增] 查詢文章列表 (ArticleController 使用)
    public List<Article> queryArticles(QueryRequest request) {
        String sql = "SELECT * FROM news_article WHERE published_at BETWEEN :start AND :end ORDER BY published_at DESC";
        
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("start", request.getStartTime())
                .addValue("end", request.getEndTime());
        
        // 自動將資料庫的 snake_case 欄位 (如 source_name) 對應到 Article 物件的 camelCase 屬性 (sourceName)
        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(Article.class));
    }

    // [新增] 更新文章 (ArticleController 使用)
    @Transactional
    public String updateArticle(UpdateRequest request) {
        if (request.getFields() == null || request.getFields().isEmpty()) {
            return "無更新欄位";
        }

        StringJoiner setClause = new StringJoiner(", ");
        MapSqlParameterSource params = new MapSqlParameterSource();
        
        try {
            params.addValue("id", Long.parseLong(request.getId()));
        } catch (NumberFormatException e) {
            return "無效的文章 ID 格式";
        }

        for (Map.Entry<String, Object> entry : request.getFields().entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            
            // 將前端傳來的 camelCase 欄位名稱轉換為資料庫的 snake_case
            String dbColumn = mapToDbColumn(fieldName);
            if (dbColumn == null) continue; // 忽略不合法的欄位

            setClause.add(dbColumn + " = :" + fieldName);
            params.addValue(fieldName, value);
        }

        if (setClause.length() == 0) {
            return "無有效更新欄位";
        }

        String sql = "UPDATE news_article SET " + setClause + " WHERE id = :id";
        int updatedCount = jdbcTemplate.update(sql, params);
        
        return updatedCount > 0 ? "更新成功" : "更新失敗，找不到該 ID 的文章";
    }

    // [新增] 刪除文章並回傳被刪除的資訊 (ArticleController 使用)
    @Transactional
    public List<Map<String, Object>> deleteArticlesWithInfo(DeleteRequest request) {
        // 1. 先撈出要刪除的資料，以便回傳給前端顯示
        String selectSql = "SELECT id, title, published_at FROM news_article WHERE published_at BETWEEN :start AND :end";
        
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("start", request.getStartTime())
                .addValue("end", request.getEndTime());

        List<Map<String, Object>> articlesToDelete = jdbcTemplate.queryForList(selectSql, params);

        if (articlesToDelete.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 執行刪除
        String deleteSql = "DELETE FROM news_article WHERE published_at BETWEEN :start AND :end";
        jdbcTemplate.update(deleteSql, params);

        return articlesToDelete;
    }

    // 輔助方法：欄位名稱映射 (Frontend -> DB)
    private String mapToDbColumn(String fieldName) {
        switch (fieldName) {
            case "id": return null; // ID 不可更新
            case "sourceName": return "source_name";
            case "urlToImage": return "url_to_image";
            case "publishedAt": return "published_at";
            case "createTime": return "create_time";
            case "updateTime": return "update_time";
            // 若欄位名稱一致 (如 title, description, content, author, url, summary, category, country) 則直接回傳
            default: return fieldName;
        }
    }
}