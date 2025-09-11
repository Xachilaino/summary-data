package com.opview.summary.entity;

import com.google.gson.annotations.SerializedName;
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

    @SerializedName("s_name")
    @Column("s_name")
    private String sName;

    @SerializedName("s_area_name")
    @Column("s_area_name")
    private String sAreaName;

    @SerializedName("page_url")
    @Column("page_url")
    private String pageUrl;

    @SerializedName("post_time")
    @Column("post_time")
    private String postTime;

    private String author;

    @SerializedName("main_id")
    @Column("main_id")
    private String mainId;

    @SerializedName("positive_percentage")
    @Column("positive_percentage")
    private float positivePercentage;

    @SerializedName("negative_percentage")
    @Column("negative_percentage")
    private float negativePercentage;

    @SerializedName("comment_count")
    @Column("comment_count")
    private int commentCount;

    @SerializedName("view_count")
    @Column("view_count")
    private int viewCount;

    @SerializedName("used_count")
    @Column("used_count")
    private int usedCount;

    @SerializedName("content_type")
    @Column("content_type")
    private String contentType;

    @SerializedName("sentiment_tag")
    @Column("sentiment_tag")
    private String sentimentTag;

    @SerializedName("_hit_num")
    @Column("_hit_num")
    private int hitNum;

    @SerializedName("article_type")
    @Column("article_type")
    private String articleType;

    @Column("create_time")
    private LocalDateTime createTime;

    @Column("update_time")
    private LocalDateTime updateTime;

    // === Getter / Setter ===
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsAreaName() {
        return sAreaName;
    }

    public void setsAreaName(String sAreaName) {
        this.sAreaName = sAreaName;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMainId() {
        return mainId;
    }

    public void setMainId(String mainId) {
        this.mainId = mainId;
    }

    public float getPositivePercentage() {
        return positivePercentage;
    }

    public void setPositivePercentage(float positivePercentage) {
        this.positivePercentage = positivePercentage;
    }

    public float getNegativePercentage() {
        return negativePercentage;
    }

    public void setNegativePercentage(float negativePercentage) {
        this.negativePercentage = negativePercentage;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(int usedCount) {
        this.usedCount = usedCount;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getSentimentTag() {
        return sentimentTag;
    }

    public void setSentimentTag(String sentimentTag) {
        this.sentimentTag = sentimentTag;
    }

    public int getHitNum() {
        return hitNum;
    }

    public void setHitNum(int hitNum) {
        this.hitNum = hitNum;
    }

    public String getArticleType() {
        return articleType;
    }

    public void setArticleType(String articleType) {
        this.articleType = articleType;
    }

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
}
