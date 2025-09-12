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

    
     // 取得昨日文章資訊並存入資料庫。
     
    public void processDailyArticles() {
        logger.info("開始取得昨天的文章資訊...");

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
            if (apiResponse != null && apiResponse.getResponseInfo() != null) {
                String errorCode = apiResponse.getResponseInfo().getErrorCode();
                String errorMessage = apiResponse.getResponseInfo().getErrorMessage();

                if ("0".equals(errorCode)) {
                    List<Article> articles = apiResponse.getResult();

                    if (articles != null && !articles.isEmpty()) {
                        logger.info("成功從 API 取得 {} 筆文章資料。", articles.size());

                        LocalDateTime now = LocalDateTime.now();
                        for (Article article : articles) {
                            // 新資料 → 補 createTime & updateTime
                            if (article.getCreateTime() == null) {
                                article.setCreateTime(now);
                            }
                            // 舊資料 → updateTime 更新
                            article.setUpdateTime(now);

                            // 使用 UPSERT 避免主鍵衝突
                            articleDao.upsert(article);
                        }

                        logger.info("已成功處理 {} 筆文章資料。", articles.size());
                    } else {
                        logger.warn("API 回應成功，但沒有取得任何文章資料。");
                    }
                } else {
                    logger.error("從 API 取得文章資料失敗。錯誤碼: {}, 錯誤訊息: {}", errorCode, errorMessage);
                }
            } else {
                // 🔹 responseInfo 為 null，直接輸出原始 JSON
                logger.error("API 回應格式異常，可能是 mapping 錯誤。原始回應: {}", apiResponse);
            }
        } catch (Exception e) {
            logger.error("資料處理過程中發生例外錯誤：", e);
        }

        logger.info("文章資料處理排程執行完畢。");
    }
}
