package com.opview.summary.controller;

import com.opview.summary.scheduler.DailyTaskScheduler;
import com.opview.summary.service.DataProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final DailyTaskScheduler dailyTaskScheduler;
    private final DataProcessingService dataProcessingService;

    @Autowired
    public TaskController(DailyTaskScheduler dailyTaskScheduler, DataProcessingService dataProcessingService) {
        this.dailyTaskScheduler = dailyTaskScheduler;
        this.dataProcessingService = dataProcessingService;
    }

    // 既有的手動排程測試
    @GetMapping("/run-task")
    public ResponseEntity<?> runScheduledTaskManually() {
        try {
            dailyTaskScheduler.runDailyTask();
            return ResponseEntity.ok(Map.of("status", "success", "message", "任務已觸發"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    /**
     * [新增] 檢查並補檔 API
     * 前端 (Vue) 在首頁載入時呼叫此 API
     */
    @PostMapping("/check-data")
    public ResponseEntity<?> checkAndBackfillData() {
        // 使用新執行緒在背景執行，避免前端卡住等待
        new Thread(() -> {
            try {
                dataProcessingService.checkAndBackfillData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "已啟動背景檢查與補檔任務"
        ));
    }
}