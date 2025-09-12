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

    @Autowired
    public ArticleService(ArticleDao articleDao, NamedParameterJdbcTemplate jdbcTemplate) {
        this.articleDao = articleDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 查詢：依時間範圍 + sentiment_tag=N
     */
    public List<Article> queryArticles(QueryRequest request) {
        String sql = """
            SELECT * FROM ts_page_content
            WHERE post_time BETWEEN :start AND :end
              AND sentiment_tag = 'N'
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("start", request.getStartTime())
                .addValue("end", request.getEndTime());

        return jdbcTemplate.query(sql, params,
                (rs, rowNum) -> {
                    Article a = new Article();
                    a.setId(rs.getString("id"));
                    a.setTitle(rs.getString("title"));
                    a.setContent(rs.getString("content"));
                    a.setsName(rs.getString("s_name"));
                    a.setsAreaName(rs.getString("s_area_name"));
                    a.setPageUrl(rs.getString("page_url"));
                    a.setPostTime(rs.getTimestamp("post_time") != null ? rs.getTimestamp("post_time").toLocalDateTime() : null);
                    a.setAuthor(rs.getString("author"));
                    a.setMainId(rs.getString("main_id"));
                    a.setSentimentTag(rs.getString("sentiment_tag"));
                    a.setUpdateTime(rs.getTimestamp("update_time") != null ? rs.getTimestamp("update_time").toLocalDateTime() : null);
                    return a;
                });
    }

    /**
     * 更新：依 ID + 動態欄位
     */
    public String updateArticle(UpdateRequest request) {
        if (request.getFields() == null || request.getFields().isEmpty()) {
            return "更新失敗：未提供任何更新欄位";
        }

        // 不允許更新的欄位
        List<String> immutableFields = List.of("id", "create_time");

        StringBuilder sql = new StringBuilder("UPDATE ts_page_content SET ");
        MapSqlParameterSource params = new MapSqlParameterSource();

        boolean hasValidField = false;
        StringBuilder ignoredFields = new StringBuilder();

        for (Map.Entry<String, Object> entry : request.getFields().entrySet()) {
            String field = entry.getKey();

            if (immutableFields.contains(field)) {
                ignoredFields.append(field).append(" ");
                continue; // 跳過不可更動欄位
            }

            sql.append(field).append(" = :").append(field).append(", ");
            params.addValue(field, entry.getValue());
            hasValidField = true;
        }

        if (!hasValidField) {
            return "更新失敗：全部欄位不可更動 (忽略: " + ignoredFields + ")";
        }

        // 強制更新 update_time
        sql.append("update_time = :updateTime WHERE id = :id");
        params.addValue("updateTime", LocalDateTime.now());
        params.addValue("id", request.getId());

        int rows = jdbcTemplate.update(sql.toString(), params);

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

    /**
     * 刪除：依時間範圍 + sentiment_tag=N，並回傳被刪除的 id 和 title
     */
    public List<Map<String, Object>> deleteArticlesWithInfo(DeleteRequest request) {
        String selectSql = """
            SELECT id, title FROM ts_page_content
            WHERE post_time BETWEEN :start AND :end
              AND sentiment_tag = 'N'
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("start", request.getStartTime())
                .addValue("end", request.getEndTime());

        // 先查出要刪除的文章
        List<Map<String, Object>> toDelete = jdbcTemplate.query(selectSql, params,
                (rs, rowNum) -> Map.of(
                        "id", rs.getString("id"),
                        "title", rs.getString("title")
                ));

        // 如果有符合條件才執行刪除
        if (!toDelete.isEmpty()) {
            String deleteSql = """
                DELETE FROM ts_page_content
                WHERE post_time BETWEEN :start AND :end
                  AND sentiment_tag = 'N'
            """;
            jdbcTemplate.update(deleteSql, params);
        }

        return toDelete;
    }
}
