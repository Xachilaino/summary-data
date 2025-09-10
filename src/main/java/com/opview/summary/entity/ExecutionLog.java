package com.opview.summary.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Table("execute_log")
// 指定對應的資料表
@Getter
@Setter
public class ExecutionLog {

 @Id 
 private Long id;
//指定流水號為主鍵
 
 private LocalDateTime startTime; // 執行開始時間
 private LocalDateTime endTime;   // 執行結束時間


}
