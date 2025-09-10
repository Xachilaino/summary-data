package com.opview.summary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 啟用 Spring 的排程功能
public class SummaryDataApplication {

	public static void main(String[] args) {
		// 啟動 Spring Boot 應用程式
		SpringApplication.run(SummaryDataApplication.class, args);
	}
}