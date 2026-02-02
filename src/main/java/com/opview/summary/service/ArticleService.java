package com.opview.summary.service;

import com.opview.summary.dao.ArticleDao;
import com.opview.summary.dto.QueryRequest;
import com.opview.summary.entity.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

@Service
public class ArticleService {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public ArticleService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 關鍵修正：改查 news_article 並將 id 轉為 Long
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
    
    // (其餘 queryArticles / updateArticle 方法請同步將表名改為 news_article)
}