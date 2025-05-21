package com.yandex.blog.repository;

import com.yandex.blog.model.Comment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JdbcNativeCommentRepository implements CommentRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcNativeCommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        return jdbcTemplate.query("select id, post_id, content from comments where post_id = ?",
                new Object[]{postId},
                (rs, rowNum) -> new Comment (
                        rs.getLong("id"),
                        rs.getLong("post_id"),
                        rs.getString("content")
                )
        );
    }

    @Override
    public void deleteAllCommentsByPostId(Long postId) {
        jdbcTemplate.update("delete from comments where post_id = ?", postId);
    }

    @Override
    public void deleteCommentById(Long id) {
        jdbcTemplate.update("delete from comments where id = ?", id);
    }

    @Override
    public void saveComment(Comment comment) {
        jdbcTemplate.update("insert into comments (post_id, content) values (?, ?)", comment.getPostId(), comment.getContent());
    }

    @Override
    public void updateComment(Comment comment) {
        jdbcTemplate.update("update comments set content = ? where id = ?", comment.getContent(), comment.getId());
    }

    @Override
    public int countByPostId(Long postId) {
        return jdbcTemplate.queryForObject("select count(*) from comments where post_id = ?",
                Integer.class,
                postId);
    }
}

