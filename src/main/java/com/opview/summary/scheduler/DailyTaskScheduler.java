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


    @Scheduled(cron = "${opview.api.cronExpression}")
    //每日自動化排程任務，參數 ${opview.api.cronExpression} 從 application.properties 中讀取
    public void runDailyTask() {
        logger.info("開始執行任務，時間：{}", LocalDateTime.now());

        ExecutionLog log = new ExecutionLog();
        log.setStartTime(LocalDateTime.now());
        
        ExecutionLog savedLog = logDao.save(log);
        // 儲存任務開始時間

        try {
            dataProcessingService.processDailyArticles();           
            // 呼叫 DataProcessingService 來執行任務
                        
            savedLog.setEndTime(LocalDateTime.now());
            logDao.save(savedLog);
            logger.info("任務執行成功，時間：{}", LocalDateTime.now());
            // 執行成功時，更新任務結束時間

        } catch (Exception e) {
            logger.error("任務執行失敗：{}", e.getMessage(), e);
            savedLog.setEndTime(LocalDateTime.now());
            logDao.save(savedLog);
            // 如果任務執行失敗，記錄錯誤訊息並更新結束時間
        }
    }
}