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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
     * 執行整個資料處理流程：取得昨日文章資訊並存入資料庫。
     */
    public void processDailyArticles() {
        logger.info("開始執行每日文章資料處理排程...");

        // 1. 計算昨日的日期範圍
        LocalDate yesterday = LocalDate.now().minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        LocalDateTime startDateTime = yesterday.atStartOfDay();
        LocalDateTime endDateTime = yesterday.atTime(LocalTime.MAX);

        String startDate = startDateTime.format(formatter);
        String endDate = endDateTime.format(formatter);

        logger.info("取得日期範圍: {} 至 {}", startDate, endDate);

        try {
            // 2. 呼叫 API
            SummaryApiResponse apiResponse = apiClient.fetchArticles(startDate, endDate);

            // 3. 檢查回應
            if (apiResponse != null && "0".equals(apiResponse.getResponseInfo().getErrorCode())) {
                List<Article> articles = apiResponse.getResult();

                if (articles != null && !articles.isEmpty()) {
                    logger.info("成功從 API 取得 {} 筆文章資料。", articles.size());

                    LocalDateTime now = LocalDateTime.now();
                    for (Article article : articles) {
                        if (articleDao.existsById(article.getId())) {
                            // 已存在 → 更新 updateTime
                            article.setUpdateTime(now);
                        } else {
                            // 新資料 → 補 createTime & updateTime
                            article.setCreateTime(now);
                            article.setUpdateTime(now);
                        }
                        articleDao.save(article);
                    }

                    logger.info("已成功處理 {} 筆文章資料。", articles.size());
                } else {
                    logger.warn("API 回應成功，但沒有取得任何文章資料。");
                }
            } else {
                String error = apiResponse != null ?
                        "錯誤碼: " + apiResponse.getResponseInfo().getErrorCode() +
                        ", 錯誤訊息: " + apiResponse.getResponseInfo().getErrorMessage() :
                        "API 回應為空或格式不正確";
                logger.error("從 API 取得文章資料失敗。原因: {}", error);
            }
        } catch (Exception e) {
            logger.error("資料處理過程中發生例外錯誤：", e);
        }

        logger.info("每日文章資料處理排程執行完畢。");
    }
}
