package com.opview.summary.dao;

import com.opview.summary.entity.Article;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleDao extends CrudRepository<Article, String> {

    /**
     * 使用 MySQL/MariaDB 的 ON DUPLICATE KEY UPDATE 做 UPSERT
     */
    @Modifying
    @Query("""
        INSERT INTO ts_page_content (
            id, title, content, s_name, s_area_name, page_url, post_time,
            author, main_id, positive_percentage, negative_percentage,
            comment_count, view_count, used_count, content_type,
            sentiment_tag, _hit_num, article_type, create_time, update_time
        ) VALUES (
            :#{#a.id}, :#{#a.title}, :#{#a.content}, :#{#a.sName}, :#{#a.sAreaName},
            :#{#a.pageUrl}, :#{#a.postTime}, :#{#a.author}, :#{#a.mainId},
            :#{#a.positivePercentage}, :#{#a.negativePercentage},
            :#{#a.commentCount}, :#{#a.viewCount}, :#{#a.usedCount},
            :#{#a.contentType}, :#{#a.sentimentTag}, :#{#a.hitNum},
            :#{#a.articleType}, :#{#a.createTime}, :#{#a.updateTime}
        )
        ON DUPLICATE KEY UPDATE
            title = VALUES(title),
            content = VALUES(content),
            s_name = VALUES(s_name),
            s_area_name = VALUES(s_area_name),
            page_url = VALUES(page_url),
            post_time = VALUES(post_time),
            author = VALUES(author),
            main_id = VALUES(main_id),
            positive_percentage = VALUES(positive_percentage),
            negative_percentage = VALUES(negative_percentage),
            comment_count = VALUES(comment_count),
            view_count = VALUES(view_count),
            used_count = VALUES(used_count),
            content_type = VALUES(content_type),
            sentiment_tag = VALUES(sentiment_tag),
            _hit_num = VALUES(_hit_num),
            article_type = VALUES(article_type),
            update_time = VALUES(update_time)
        """)
    void upsert(@Param("a") Article article);
}
