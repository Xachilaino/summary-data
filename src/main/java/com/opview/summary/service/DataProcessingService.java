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
        // 取得昨天的日期 (NewsAPI 免費版通常只能查最近一個月的資料)
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String fromDate = yesterday.toString(); // yyyy-MM-dd

        logger.info("開始處理新聞資料，日期: {}", fromDate);

        NewsResponseDto response = apiClient.fetchArticles(fromDate);

        if (response != null && response.getArticles() != null) {
            logger.info("取得 {} 筆原始新聞資料", response.getArticles().size());

            for (NewsArticleDto dto : response.getArticles()) {
                // 資料轉換: DTO -> Entity
                Article article = new Article();
                
                // 處理可能為 null 的欄位
                article.setTitle(dto.getTitle() != null ? dto.getTitle() : "No Title");
                article.setUrl(dto.getUrl());
                article.setDescription(dto.getDescription());
                article.setContent(dto.getContent());
                article.setAuthor(dto.getAuthor());
                article.setUrlToImage(dto.getUrlToImage());
                
                if (dto.getSource() != null) {
                    article.setSourceName(dto.getSource().getName());
                }

                // 時間處理
                try {
                    // NewsAPI 回傳的是 ISO_INSTANT (e.g., 2023-10-25T10:30:00Z)
                    if (dto.getPublishedAt() != null) {
                        LocalDateTime pubTime = LocalDateTime.parse(dto.getPublishedAt(), DateTimeFormatter.ISO_DATE_TIME);
                        article.setPublishedAt(pubTime);
                    }
                } catch (Exception e) {
                    logger.warn("日期解析失敗: {}", dto.getPublishedAt());
                    article.setPublishedAt(LocalDateTime.now());
                }

                article.setCreateTime(LocalDateTime.now());
                article.setUpdateTime(LocalDateTime.now());

                // 寫入資料庫
                try {
                    articleDao.upsert(article);
                } catch (Exception e) {
                    logger.error("寫入新聞失敗: {}", article.getTitle(), e);
                }
            }
            logger.info("新聞資料處理完成。");
        } else {
            logger.warn("未取得任何新聞資料。");
        }
    }
}