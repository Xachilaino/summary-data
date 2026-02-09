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

    /**
     * 每日排程執行的任務 (抓取昨天的資料，使用預設設定)
     */
    public void processDailyArticles() {
        String fromDate = LocalDate.now().minusDays(1).toString(); 
        logger.info("開始每日抓取任務，日期: {}", fromDate);
        NewsResponseDto response = apiClient.fetchArticles(fromDate); // 使用預設參數
        saveArticles(response);
    }

    /**
     * [新增] 檢查並補檔邏輯
     * 1. 檢查 2 天前有沒有資料
     * 2. 若無，迴圈抓取前 7 天資料 (pageSize=100, sortBy=popularity)
     */
    public void checkAndBackfillData() {
        LocalDate twoDaysAgo = LocalDate.now().minusDays(2);
        int count = articleDao.countArticlesByDate(twoDaysAgo);
        
        logger.info("檢查日期 {} 的資料量: {}", twoDaysAgo, count);

        if (count == 0) {
            logger.warn("發現資料缺漏！開始執行『7日熱門新聞補檔』任務...");
            
            // 迴圈抓取過去 7 天 (從昨天開始往回推)
            for (int i = 1; i <= 7; i++) {
                String targetDate = LocalDate.now().minusDays(i).toString();
                logger.info("正在補抓 {} 的熱門新聞 (100筆, 熱度排序)...", targetDate);
                
                // 呼叫 API: 日期, pageSize=100, sortBy=popularity
                NewsResponseDto response = apiClient.fetchArticles(targetDate, 100, "popularity");
                
                saveArticles(response);
                
                // 禮貌性暫停 1 秒，避免被 API 視為攻擊
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            }
            logger.info("7日補檔任務完成！");
        } else {
            logger.info("資料完整，無需補檔。");
        }
    }

    /**
     * 統一儲存邏輯，避免代碼重複
     */
    private void saveArticles(NewsResponseDto response) {
        if (response != null && "ok".equals(response.getStatus()) && response.getArticles() != null) {
            logger.info("成功取得 {} 筆新聞", response.getArticles().size());
            for (NewsArticleDto dto : response.getArticles()) {
                Article article = new Article();
                article.setTitle(dto.getTitle());
                article.setDescription(dto.getDescription());
                article.setUrl(dto.getUrl());
                article.setSourceName(dto.getSource() != null ? dto.getSource().getName() : "Unknown");
                
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