package com.opview.summary.dao;

import com.opview.summary.entity.Article;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * 負責與 ts_page_content 資料表互動的資料存取介面。
 * 繼承 CrudRepository 以獲得基本的 CRUD 操作。
 */
@Repository
public interface ArticleDao extends CrudRepository<Article, String> {

    // CrudRepository 提供了 save(), findAll() 等方法，無需在此處額外定義。
    // 如果需要自訂查詢，可以在此處添加，例如：
    // List<Article> findByAuthor(String author);
}