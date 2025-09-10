package com.opview.summary.controller;

import com.opview.summary.scheduler.DailyTaskScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api") // 設定 API 的基礎路徑
public class TaskController {

    private final DailyTaskScheduler dailyTaskScheduler;

    @Autowired
    public TaskController(DailyTaskScheduler dailyTaskScheduler) {
        this.dailyTaskScheduler = dailyTaskScheduler;
    }

    /**
     * 提供一個 API 端點，用於手動觸發每日任務。
     * 訪問 URL: http://localhost:8080/api/run-task
     * @return 任務執行結果訊息
     */
    @GetMapping("/run-task")
    public ResponseEntity<String> runScheduledTaskManually() {
        // 呼叫排程器中的方法來執行任務
        dailyTaskScheduler.runDailyTask();
        return ResponseEntity.ok("排程任務已手動觸發並開始執行！");
    }
}