package com.opview.summary.dto.news;

import java.util.List;

public class NewsResponseDto {
    private String status;
    private int totalResults;
    private List<NewsArticleDto> articles;

    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getTotalResults() { return totalResults; }
    public void setTotalResults(int totalResults) { this.totalResults = totalResults; }
    public List<NewsArticleDto> getArticles() { return articles; }
    public void setArticles(List<NewsArticleDto> articles) { this.articles = articles; }
}