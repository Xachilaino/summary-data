package com.opview.summary.controller;

import com.opview.summary.scheduler.DailyTaskScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final DailyTaskScheduler dailyTaskScheduler;

    @Autowired
    public TaskController(DailyTaskScheduler dailyTaskScheduler) {
        this.dailyTaskScheduler = dailyTaskScheduler;
    }

    /*
     * 訪問 URL: http://localhost:8080/api/run-task
     */
    @GetMapping("/run-task")
    public ResponseEntity<String> runScheduledTaskManually() {
        try {
            dailyTaskScheduler.runDailyTask();
            return ResponseEntity.ok("任務已觸發");
        } catch (Exception e) {
            // 印出錯誤訊息
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("手動觸發任務失敗，原因: " + e.getMessage());
        }
    }
}
