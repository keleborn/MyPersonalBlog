package com.yandex.blog.test.unit;

import com.yandex.blog.model.Comment;
import com.yandex.blog.repository.JdbcNativeCommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommentRepositoryUnitTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private JdbcNativeCommentRepository jdbcNativeCommentRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCommentsByPostId_shouldExecuteQueryForAllCommentsByPostId() {
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment(1L, 1L, "Test comment 1"));
        comments.add(new Comment(2L, 1L, "Test comment 2"));
        comments.add(new Comment(3L, 1L, "Test comment 3"));

        when(jdbcTemplate.query(anyString(), any(Object[].class), any(RowMapper.class))).thenReturn(comments);

        List<Comment> result = jdbcNativeCommentRepository.getCommentsByPostId(1L);

        assertEquals(3, result.size());
        verify(jdbcTemplate, times(1)).query(
                eq("select id, post_id, content from comments where post_id = ?"),
                        any(Object[].class),
                        any(RowMapper.class));
    }

    @Test
    void deleteAllCommentsByPostId_shouldExecuteDeleteQueryByPostId() {
        jdbcNativeCommentRepository.deleteAllCommentsByPostId(1L);

        verify(jdbcTemplate, times(1)).update(eq("delete from comments where post_id = ?"), eq(1L));
    }

    @Test
    void deleteCommentById_shoudlExecuteDeleteQueryById() {
        jdbcNativeCommentRepository.deleteCommentById(1L);

        verify(jdbcTemplate, times(1)).update(eq("delete from comments where id = ?"), eq(1L));
    }

    @Test
    void saveComment_shouldExecuteInsertQuery() {
        jdbcNativeCommentRepository.saveComment(new Comment(1L, 1L, "Test comment 1"));

        verify(jdbcTemplate, times(1)).update(
                eq("insert into comments (post_id, content) values (?, ?)"),
                eq(1L),
                eq("Test comment 1"));
    }

    @Test
    void updateComment_shouldExecuteUpdateQuery() {
        jdbcNativeCommentRepository.updateComment(new Comment(1L, 1L, "Test comment 1"));

        verify(jdbcTemplate, times(1)).update(
                eq("update comments set content = ? where id = ?"),
                eq("Test comment 1"),
                eq(1L));
    }

    @Test
    void countByPostId_shouldExecuteCountQuery() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), any(Object[].class))).thenReturn(1);

        jdbcNativeCommentRepository.countByPostId(1L);

        verify(jdbcTemplate, times(1)).queryForObject(
                eq("select count(*) from comments where post_id = ?"),
                eq(Integer.class),
                eq(1L)
        );
    }
}
