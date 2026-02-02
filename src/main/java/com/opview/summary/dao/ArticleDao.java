package com.opview.summary.dao;

import com.opview.summary.entity.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ArticleDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public ArticleDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void upsert(Article article) {
        // 基於 URL 判斷：如果 URL 已存在則更新，否則新增
        String sql = "INSERT INTO news_article " +
                "(source_name, author, title, description, url, url_to_image, published_at, content, create_time, update_time) " +
                "VALUES " +
                "(:sourceName, :author, :title, :description, :url, :urlToImage, :publishedAt, :content, :createTime, :updateTime) " +
                "ON DUPLICATE KEY UPDATE " +
                "title = :title, " +
                "description = :description, " +
                "url_to_image = :urlToImage, " +
                "update_time = :updateTime";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("sourceName", article.getSourceName());
        params.addValue("author", article.getAuthor());
        params.addValue("title", article.getTitle());
        params.addValue("description", article.getDescription());
        params.addValue("url", article.getUrl());
        params.addValue("urlToImage", article.getUrlToImage());
        params.addValue("publishedAt", article.getPublishedAt());
        params.addValue("content", article.getContent());
        params.addValue("createTime", article.getCreateTime());
        params.addValue("updateTime", article.getUpdateTime());

        jdbcTemplate.update(sql, params);
    }
    
    // 這裡可以保留或新增其他查詢方法，例如 findAll, findByDate 等，視後續需求而定
}