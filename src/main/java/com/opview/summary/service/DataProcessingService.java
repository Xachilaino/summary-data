package com.opview.summary.service;

import com.opview.summary.dao.ArticleDao;
import com.opview.summary.entity.Article;
import com.opview.summary.entity.SummaryApiResponse;
import com.opview.summary.util.ApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service // 標記為 Spring Service，讓 Spring 管理這個組件
public class DataProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(DataProcessingService.class);

    private final ApiClient apiClient;
    private final ArticleDao articleDao;

    // 透過建構子注入相依性
    @Autowired
    public DataProcessingService(ApiClient apiClient, ArticleDao articleDao) {
        this.apiClient = apiClient;
        this.articleDao = articleDao;
    }

    /**
     * 執行整個資料處理流程：取得昨日文章資訊並存入資料庫。
     */
    public void processDailyArticles() {
        logger.info("開始執行每日文章資料處理排程...");

        // 1. 計算昨日的日期範圍
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String startDate = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String endDate = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        logger.info("取得日期範圍: {} 至 {}", startDate, endDate);

        try {
            // 2. 呼叫 ApiClient 取得 API 回應
            SummaryApiResponse apiResponse = apiClient.fetchArticles(startDate, endDate);

            // 3. 檢查 API 回應是否成功
            if (apiResponse != null && "0000".equals(apiResponse.getResponseInfo().getErrorCode())) {
                List<Article> articles = apiResponse.getResult();
                
                if (articles != null && !articles.isEmpty()) {
                    logger.info("成功從 API 取得 {} 筆文章資料。", articles.size());

                    // 4. 使用 ArticleDao 將資料存入資料庫
                    articleDao.saveAll(articles);
                    
                    logger.info("已成功將 {} 筆文章資料存入資料庫。", articles.size());
                } else {
                    logger.warn("API 回應成功，但沒有取得任何文章資料。");
                }
            } else {
                // 處理 API 呼叫失敗的情況
                String error = apiResponse != null ?
                               "錯誤碼: " + apiResponse.getResponseInfo().getErrorCode() +
                               ", 錯誤訊息: " + apiResponse.getResponseInfo().getErrorMessage() :
                               "API 回應為空或格式不正確";
                logger.error("從 API 取得文章資料失敗。原因: {}", error);
            }
        } catch (Exception e) {
            // 處理在執行過程中發生的任何例外
            logger.error("資料處理過程中發生例外錯誤：", e);
        }

        logger.info("每日文章資料處理排程執行完畢。");
    }
}
