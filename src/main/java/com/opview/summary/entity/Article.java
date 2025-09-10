package com.opview.summary.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
* 代表 API 回應的單篇文章資訊，並映射到資料庫的 ts_page_content 資料表。
*/
@Table("ts_page_content") // 指定對應的資料表名稱 
@Getter // 自動生成所有欄位的 Getter
@Setter // 自動生成所有欄位的 Setter
public class Article {

 @Id // Spring Data JDBC 辨識此欄位為主鍵 
 private String id; // 文章ID 

 private String title; // 標題 
 private String content; // 內文 

 @Column("s_name") // 映射到資料庫的 s_name 欄位 
 private String sName;

 @Column("s_area_name") // 映射到資料庫的 s_area_name 欄位 
 private String sAreaName;

 @Column("page_url") // 映射到資料庫的 page_url 欄位 
 private String pageUrl;

 @Column("post_time") // 映射到資料庫的 post_time 欄位 
 private String postTime;

 private String author; // 作者 

 @Column("main_id") // 映射到資料庫的 main_id 欄位 
 private String mainId;

 @Column("positive_percentage") // 映射到資料庫的 positive_percentage 欄位 
 private float positivePercentage;

 @Column("negative_percentage") // 映射到資料庫的 negative_percentage 欄位 
 private float negativePercentage;

 @Column("comment_count") // 映射到資料庫的 comment_count 欄位 
 private int commentCount;

 @Column("view_count") // 映射到資料庫的 view_count 欄位 
 private int viewCount;

 @Column("used_count") // 映射到資料庫的 used_count 欄位 
 private int usedCount;

 @Column("content_type") // 映射到資料庫的 content_type 欄位 
 private String contentType;

 @Column("sentiment_tag") // 映射到資料庫的 sentiment_tag 欄位 
 private String sentimentTag;

 @Column("_hit_num") // 映射到資料庫的 _hit_num 欄位 
 private int hitNum;

 @Column("article_type") // 映射到資料庫的 article_type 欄位 
 private String articleType;

 @Column("create_time") // 文章資訊初次存入時間 
 private LocalDateTime createTime;

 @Column("update_time") // 文章資訊最後更新時間 
 private LocalDateTime updateTime;
}