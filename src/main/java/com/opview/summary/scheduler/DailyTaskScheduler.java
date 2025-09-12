package com.opview.summary.scheduler;

import com.opview.summary.dao.LogDao;
import com.opview.summary.entity.ExecutionLog;
import com.opview.summary.service.DataProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DailyTaskScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DailyTaskScheduler.class);

    private final LogDao logDao;
    private final DataProcessingService dataProcessingService;

    @Autowired
    public DailyTaskScheduler(LogDao logDao, DataProcessingService dataProcessingService) {
        this.logDao = logDao;
        this.dataProcessingService = dataProcessingService;
    }

    /**
     * 每日自動化排程任務
     * cron = "0 0 10 * * ?" 表示每日上午10:00執行
     * 參數 ${opview.api.cronExpression} 從 application.properties 中讀取
     */
    @Scheduled(cron = "${opview.api.cronExpression}")
    public void runDailyTask() {
        logger.info("開始執行任務，時間：{}", LocalDateTime.now());

        ExecutionLog log = new ExecutionLog();
        log.setStartTime(LocalDateTime.now());
        
        // 儲存任務開始時間的紀錄
        ExecutionLog savedLog = logDao.save(log);

        try {
            // 呼叫 DataProcessingService 來執行核心業務邏輯
            dataProcessingService.processDailyArticles();
            
            // 任務成功完成後，記錄結束時間
            savedLog.setEndTime(LocalDateTime.now());
            logDao.save(savedLog);
            logger.info("任務執行成功，時間：{}", LocalDateTime.now());

        } catch (Exception e) {
            // 如果任務執行失敗，記錄錯誤訊息並更新結束時間
            logger.error("任務執行失敗：{}", e.getMessage(), e);
            savedLog.setEndTime(LocalDateTime.now());
            logDao.save(savedLog);
        }
    }
}