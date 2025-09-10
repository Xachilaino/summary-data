package com.opview.summary.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 代表任務執行日誌的實體，並映射到資料庫的 execute_log 資料表。
 */
@Table("execute_log")
public class ExecutionLog {

    @Id
    private Long id; // 指定流水號為主鍵

    private LocalDateTime startTime; // 執行開始時間
    private LocalDateTime endTime;   // 執行結束時間

    // 手動為所有欄位撰寫 Getter 和 Setter 方法

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}