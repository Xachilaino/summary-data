package com.opview.summary.dto.news;

public class NewsArticleDto {
    private NewsSourceDto source;
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt; // NewsAPI 回傳的是 ISO 8601 字串
    private String content;

    // Getters and Setters
    public NewsSourceDto getSource() { return source; }
    public void setSource(NewsSourceDto source) { this.source = source; }
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
    public String getPublishedAt() { return publishedAt; }
    public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}