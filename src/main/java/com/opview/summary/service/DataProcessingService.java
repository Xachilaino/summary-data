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

    
     
     
    public void processDailyArticles() {
        logger.info("開始取得昨天的文章資訊...");

        LocalDate yesterday = LocalDate.now().minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        //取得今天的日期並減去1天，定義符合 Summary API 規格的格式

        LocalDateTime startDateTime = yesterday.atStartOfDay();
        LocalDateTime endDateTime = yesterday.atTime(LocalTime.MAX);
        //定義為yesterday的00:00:00和23:59:59

        String startDate = startDateTime.format(formatter);
        String endDate = endDateTime.format(formatter);
        //轉換成符合 Summary API 規格的格式

        logger.info("取得日期範圍: {} 至 {}", startDate, endDate);

        try {
            SummaryApiResponse apiResponse = apiClient.fetchArticles(startDate, endDate);
            // 呼叫 ApiClient 的方法，並傳入計算好的時間範圍

            if (apiResponse != null && apiResponse.getResponseInfo() != null) {
                String errorCode = apiResponse.getResponseInfo().getErrorCode();
                String errorMessage = apiResponse.getResponseInfo().getErrorMessage();
                // 確認 response 是否為 null

                if ("0".equals(errorCode)) {
                    List<Article> articles = apiResponse.getResult();
                    // 若請求成功就從 apiResponse 中取得 result 陣列

                    if (articles != null && !articles.isEmpty()) {
                        logger.info("成功從 API 取得 {} 筆文章資料。", articles.size());

                        LocalDateTime now = LocalDateTime.now();
                        for (Article article : articles) {
                            if (article.getCreateTime() == null) {
                                article.setCreateTime(now);
                            // 如果文章的創建時間為 null，就將其設為現在的時間
                            }
                            article.setUpdateTime(now);
                            articleDao.upsert(article);
                            // 將更新時間設為現在的時間
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
