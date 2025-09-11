package com.opview.summary.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("ts_page_content")
public class Article {

    @Id
    private String id;

    private String title;
    private String content;

    @Column("s_name")
    private String sName;

    @Column("s_area_name")
    private String sAreaName;

    @Column("page_url")
    private String pageUrl;

    @Column("post_time")
    private LocalDateTime postTime; // ⚠️ 建議改成 LocalDateTime

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
    private LocalDateTime createTime;

    @Column("update_time")
    private LocalDateTime updateTime;

    // Getter / Setter ...
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
