package com.opview.summary.service;

import com.opview.summary.dao.ArticleDao;
import com.opview.summary.entity.Article;
import com.opview.summary.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    private final ArticleDao articleDao;

    @Autowired
    public ArticleService(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }

    /**
     * 查詢指定期間內 sentiment_tag = 'N' 的文章
     */
    public List<Article> queryArticles(String startTime, String endTime) {
        LocalDateTime start = DateTimeUtil.parse(startTime);
        LocalDateTime end = DateTimeUtil.parse(endTime);

        Iterable<Article> all = articleDao.findAll();
        return toList(all).stream()
                .filter(a -> "N".equalsIgnoreCase(a.getSentimentTag()))
                .filter(a -> {
                    LocalDateTime postDt = DateTimeUtil.parse(a.getPostTime());
                    return postDt != null &&
                           !postDt.isBefore(start) &&
                           !postDt.isAfter(end);
                })
                .collect(Collectors.toList());
    }

    /**
     * 更新單篇文章
     */
    public boolean updateArticle(String id, Map<String, Object> fields) {
        Optional<Article> optional = articleDao.findById(id);
        if (optional.isEmpty()) {
            return false;
        }

        Article article = optional.get();

        fields.forEach((key, value) -> {
            try {
                var field = Article.class.getDeclaredField(key);
                field.setAccessible(true);
                field.set(article, value);
            } catch (Exception e) {
                // 忽略不存在或型別不符的欄位
            }
        });

        article.setUpdateTime(LocalDateTime.now());
        articleDao.upsert(article);
        return true;
    }

    /**
     * 刪除指定期間內 sentiment_tag = 'N' 的文章
     */
    public int deleteArticles(String startTime, String endTime) {
        LocalDateTime start = DateTimeUtil.parse(startTime);
        LocalDateTime end = DateTimeUtil.parse(endTime);

        Iterable<Article> all = articleDao.findAll();
        List<Article> toDelete = toList(all).stream()
                .filter(a -> "N".equalsIgnoreCase(a.getSentimentTag()))
                .filter(a -> {
                    LocalDateTime postDt = DateTimeUtil.parse(a.getPostTime());
                    return postDt != null &&
                           !postDt.isBefore(start) &&
                           !postDt.isAfter(end);
                })
                .collect(Collectors.toList());

        toDelete.forEach(article -> articleDao.deleteById(article.getId()));
        return toDelete.size();
    }

    private List<Article> toList(Iterable<Article> iterable) {
        List<Article> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }
}
