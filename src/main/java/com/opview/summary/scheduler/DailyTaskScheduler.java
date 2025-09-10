package com.opview.summary.scheduler;

import com.opview.summary.dao.LogDao;
import com.opview.summary.entity.ExecutionLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DailyTaskScheduler {

    private final LogDao logDao;
    
    @Autowired
    public DailyTaskScheduler(LogDao logDao) {
        this.logDao = logDao;
    }

    public void runDailyTask() {
        ExecutionLog log = new ExecutionLog();
        log.setStartTime(LocalDateTime.now());
        
        // 儲存開始時間
        ExecutionLog savedLog = logDao.save(log);

        try {
            // 執行主要任務...
            // 例如：dataProcessingService.processData();
            
            // 任務完成後，更新結束時間並儲存
            savedLog.setEndTime(LocalDateTime.now());
            logDao.save(savedLog);
            
        } catch (Exception e) {
            // 處理錯誤，並記錄結束時間
            savedLog.setEndTime(LocalDateTime.now());
            logDao.save(savedLog);
        }
    }
}