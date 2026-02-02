package com.opview.summary.controller;

import com.opview.summary.scheduler.DailyTaskScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final DailyTaskScheduler dailyTaskScheduler;

    @Autowired
    public TaskController(DailyTaskScheduler dailyTaskScheduler) {
        this.dailyTaskScheduler = dailyTaskScheduler;
    }

    
    //訪問 URL: http://localhost:8080/api/run-task
    
    @GetMapping("/run-task")
    public ResponseEntity<?> runScheduledTaskManually() {
        try {
            dailyTaskScheduler.runDailyTask();

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "任務已觸發");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "手動觸發任務失敗");
            error.put("reason", e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error);
        }
    }
}
