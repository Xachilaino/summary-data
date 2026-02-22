package com.opview.summary.dao;

import com.opview.summary.entity.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

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

    /**
     * [新增] 檢查指定日期是否有新聞資料
     * @param date 指定日期 (例如 2026-02-08)
     * @return 該日期的文章數量
     */
    public int countArticlesByDate(LocalDate date) {
        // 這裡使用 DATE() 函數確保只比對日期部分
        String sql = "SELECT COUNT(*) FROM news_article WHERE DATE(published_at) = :date";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("date", date);

        Integer count = jdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null ? count : 0;
    }
}