package com.opview.summary.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("news_article") // 表名稱改為 news_article
public class Article {

    @Id
    private Long id; // 改用 Long 自增主鍵，管理比較方便

    @Column("source_name")
    private String sourceName; // 來源媒體名稱 (如 BBC News)

    private String author;

    private String title;

    @Column("description")
    private String description; // 新聞簡介

    @Column("url")
    private String url; // 原文連結

    @Column("url_to_image")
    private String urlToImage; // 圖片連結

    @Column("published_at")
    private LocalDateTime publishedAt; // 發布時間

    @Column("content")
    private String content; // 內文 (NewsAPI 免費版通常只有截錄)

    @Column("summary")
    private String summary; // **新增**：給 Gemini 填寫總結的欄位

    @Column("create_time")
    private LocalDateTime createTime;

    @Column("update_time")
    private LocalDateTime updateTime;

    // === Getters / Setters ===
    // (你可以使用 IDE 自動生成，這裡省略以節省篇幅)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getUrlToImage() { return urlToImage; }
    public void setUrlToImage(String urlToImage) { this.urlToImage = urlToImage; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}