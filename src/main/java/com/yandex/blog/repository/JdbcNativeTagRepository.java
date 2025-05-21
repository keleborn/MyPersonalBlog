package com.yandex.blog.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JdbcNativeTagRepository implements TagRepository {
    private JdbcTemplate jdbcTemplate;

    public JdbcNativeTagRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<String> findAllTagsByPostId(long postId) {
        return jdbcTemplate.query("select id, name from tags t join post_tag_links pt on t.id = pt.tag_id where pt.post_id = ?",
                new Object[]{postId},
                (rs, rowNum) -> rs.getString("name")
        );
    }

    @Override
    public List<String> findTagByName(String name) {
        return jdbcTemplate.query("select name from tags where name = ?", new Object[]{name}, (rs, rowNum) -> rs.getString("name"));
    }

    @Override
    public List<Long> findTagIdsByNames(List<String> names) {
        String sql = String.format("select id from tags where name in ('%s')", String.join("','", names));
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("id"));
    }

    @Override
    public void save(String name) {
        jdbcTemplate.update("insert into tags (name) values (?)", name);
    }

    @Override
    public void deleteTagsForPost(long postId) {
        jdbcTemplate.update("delete from post_tag_links where post_id = ?", postId);
    }

    @Override
    public void attachTags(long postId, List<Long> tagIds) {
        for (Long tagId : tagIds) {
            jdbcTemplate.update("insert into post_tag_links (post_id, tag_id) values (?, ?)", postId, tagId);
        }
    }
}
