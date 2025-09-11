package com.opview.summary.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Getter // 自動生成大部分欄位的 Getter
@Setter // 自動生成大部分欄位的 Setter
public class Article {

    @Id // Spring Data JDBC 辨識此欄位為主鍵 
    private String id; // 文章ID 

    private String title; // 標題 
    private String content; // 內文 

    @Column("s_name")
    private String sName;

    @Column("s_area_name")
    private String sAreaName;

    @Column("page_url")
    private String pageUrl;

    @Column("post_time")
    private String postTime;

    private String author;

    @Column("main_id")
    private String mainId;

    @Column("positive_percentage")
    private float positivePercentage;

    @Column("negative_percentage")
    private float negativePercentage;

    @Column("comment_count")
    private int commentCount;

    @Column("view_count")
    private int viewCount;

    @Column("used_count")
    private int usedCount;

    @Column("content_type")
    private String contentType;

    @Column("sentiment_tag")
    private String sentimentTag;

    @Column("_hit_num")
    private int hitNum;

    @Column("article_type")
    private String articleType;

    @Column("create_time")
    private transient LocalDateTime createTime;

    @Column("update_time")
    private transient LocalDateTime updateTime;

    // 🔹 手動補上 Getter/Setter，避免 IDE 報錯
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
