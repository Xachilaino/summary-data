package com.opview.summary.service;

import com.opview.summary.dao.ArticleDao;
import com.opview.summary.dto.QueryRequest;
import com.opview.summary.dto.UpdateRequest;
import com.opview.summary.dto.DeleteRequest;
import com.opview.summary.entity.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ArticleService {

    private final ArticleDao articleDao;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    // 允許使用具名參數的 JdbcTemplate

    @Autowired
    public ArticleService(ArticleDao articleDao, NamedParameterJdbcTemplate jdbcTemplate) {
        this.articleDao = articleDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    //查詢文章
    public List<Article> queryArticles(QueryRequest request) {
        String sql = """
            SELECT * FROM ts_page_content
            WHERE post_time BETWEEN :start AND :end
              AND sentiment_tag = 'N'
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("start", request.getStartTime())
                .addValue("end", request.getEndTime());
                //設定 SQL 語句中的具名參數

        return jdbcTemplate.query(sql, params,
                (rs, rowNum) -> {
                    Article a = new Article();
                    //將資料庫回傳的 ResultSet 轉換為 Article 物件
                    a.setId(rs.getString("id"));
                    a.setTitle(rs.getString("title"));
                    a.setContent(rs.getString("content"));
                    a.setsName(rs.getString("s_name"));
                    a.setsAreaName(rs.getString("s_area_name"));
                    a.setPageUrl(rs.getString("page_url"));
                    a.setPostTime(rs.getTimestamp("post_time") != null
                            ? rs.getTimestamp("post_time").toLocalDateTime()
                            : null);
                    a.setAuthor(rs.getString("author"));
                    a.setMainId(rs.getString("main_id"));
                    a.setSentimentTag(rs.getString("sentiment_tag"));
                    a.setUpdateTime(rs.getTimestamp("update_time") != null
                            ? rs.getTimestamp("update_time").toLocalDateTime()
                            : null);
                    return a;
                });
    }

    //更新文章
    public String updateArticle(UpdateRequest request) {
        if (request.getFields() == null || request.getFields().isEmpty()) {
            return "更新失敗：未提供任何更新欄位";
        }

        List<String> immutableFields = List.of("id", "create_time");
        //定義不可被修改的欄位

        StringBuilder sql = new StringBuilder("UPDATE ts_page_content SET ");
        MapSqlParameterSource params = new MapSqlParameterSource();
        //動態拼接 SQL 語句

        boolean hasValidField = false;
        StringBuilder ignoredFields = new StringBuilder();

        for (Map.Entry<String, Object> entry : request.getFields().entrySet()) {
            String field = entry.getKey();
            //遍歷前端傳來的所有要更新的欄位

            if (immutableFields.contains(field)) {
                ignoredFields.append(field).append(" ");
                continue;
                //忽略不可被修改的欄位
            }

            sql.append(field).append(" = :").append(field).append(", ");
            params.addValue(field, entry.getValue());
            hasValidField = true;
            //將可更新的欄位添加到 SQL 語句中
        }

        if (!hasValidField) {
            return "更新失敗：全部欄位不可更動 (忽略: " + ignoredFields + ")";
        }

        sql.append("update_time = :updateTime WHERE id = :id");
        params.addValue("updateTime", LocalDateTime.now());
        params.addValue("id", request.getId());
        //將更新時間和 ID 添加到 SQL 語句中

        int rows = jdbcTemplate.update(sql.toString(), params);
        //執行 SQL 更新

        if (rows > 0) {
            if (ignoredFields.length() > 0) {
                return "部分更新成功，但以下欄位不可更動已被忽略: " + ignoredFields;
            } else {
                return "更新成功";
            }
        } else {
            return "更新失敗：找不到指定 ID";
        }
    }

    //刪除文章
    public List<Map<String, Object>> deleteArticlesWithInfo(DeleteRequest request) {
        String selectSql = """
            SELECT id, title FROM ts_page_content
            WHERE post_time BETWEEN :start AND :end
              AND sentiment_tag = 'N'
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("start", request.getStartTime())
                .addValue("end", request.getEndTime());
                //設定 SQL 語句中的具名參數

        List<Map<String, Object>> toDelete = jdbcTemplate.query(selectSql, params,
                (rs, rowNum) -> Map.of(
                        "id", rs.getString("id"),
                        "title", rs.getString("title")
                        //將資料庫回傳的 ResultSet 轉換為 Map 物件
                ));

        if (!toDelete.isEmpty()) {
            String deleteSql = """
                DELETE FROM ts_page_content
                WHERE post_time BETWEEN :start AND :end
                  AND sentiment_tag = 'N'
            """;
            jdbcTemplate.update(deleteSql, params);
            //如果有符合條件的文章，執行 SQL 刪除
        }

        return toDelete;
    }

    

     //查詢指定時間範圍的前 10 筆文章內容(回傳 id + title + content) 給 Gemini 用    
    public List<Map<String, String>> findTop10Contents(LocalDateTime start, LocalDateTime end) {
        String sql = """
            SELECT id, title, content
            FROM ts_page_content
            WHERE post_time BETWEEN :start AND :end
            ORDER BY post_time DESC
            LIMIT 10
        """;
        //取出前 10 筆最新的文章

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("start", start)
                .addValue("end", end);
                //設定 SQL 語句中的具名參數

        return jdbcTemplate.query(sql, params,
                (rs, rowNum) -> Map.of(
                        "id", rs.getString("id"),
                        "title", rs.getString("title"),
                        "content", rs.getString("content")
                        //將資料庫回傳的 ResultSet 轉換為 Map 物件
                ));
    }
}
