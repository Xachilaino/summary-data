package com.opview.summary.service;

import com.opview.summary.dao.ArticleDao;
import com.opview.summary.dto.news.NewsArticleDto;
import com.opview.summary.dto.news.NewsResponseDto;
import com.opview.summary.entity.Article;
import com.opview.summary.util.ApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DataProcessingService {
    private static final Logger logger = LoggerFactory.getLogger(DataProcessingService.class);
    private final ApiClient apiClient;
    private final ArticleDao articleDao;

    @Autowired
    public DataProcessingService(ApiClient apiClient, ArticleDao articleDao) {
        this.apiClient = apiClient;
        this.articleDao = articleDao;
    }

    public void processDailyArticles() {
        // 設定抓取日為昨天 (2026-02-01)
        String fromDate = LocalDate.now().minusDays(1).toString(); 
        logger.info("開始抓取新聞，日期範圍從: {}", fromDate);

        NewsResponseDto response = apiClient.fetchArticles(fromDate);

        if (response != null && "ok".equals(response.getStatus()) && response.getArticles() != null) {
            logger.info("成功取得 {} 筆新聞", response.getArticles().size());
            for (NewsArticleDto dto : response.getArticles()) {
                Article article = new Article();
                article.setTitle(dto.getTitle());
                article.setDescription(dto.getDescription());
                article.setUrl(dto.getUrl());
                article.setSourceName(dto.getSource() != null ? dto.getSource().getName() : "Unknown");
                
                // 解析時間 (NewsAPI 使用 ISO 格式)
                if (dto.getPublishedAt() != null) {
                    article.setPublishedAt(LocalDateTime.parse(dto.getPublishedAt(), DateTimeFormatter.ISO_DATE_TIME));
                }
                
                article.setCreateTime(LocalDateTime.now());
                article.setUpdateTime(LocalDateTime.now());
                
                articleDao.upsert(article);
            }
        } else {
            logger.warn("未抓取到任何資料。");
        }
    }
}