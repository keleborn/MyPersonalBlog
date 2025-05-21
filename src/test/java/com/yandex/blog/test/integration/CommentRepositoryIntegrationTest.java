package com.yandex.blog.test.integration;

import com.yandex.blog.configuration.DataSourceConfiguration;
import com.yandex.blog.model.Comment;
import com.yandex.blog.model.Post;
import com.yandex.blog.repository.CommentRepository;
import com.yandex.blog.repository.JdbcNativeCommentRepository;
import com.yandex.blog.repository.JdbcNativePostRepository;
import com.yandex.blog.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, JdbcNativeCommentRepository.class})
@TestPropertySource(locations = "classpath:test-application.properties")
public class CommentRepositoryIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.execute("DELETE FROM comments");
        jdbcTemplate.execute("alter table comments alter column id restart with 1");
        jdbcTemplate.execute("insert into comments(post_id, content) values (1, 'Comment1')");
        jdbcTemplate.execute("insert into comments(post_id, content) values (1, 'Comment2')");
        jdbcTemplate.execute("insert into comments(post_id, content) values (2, 'Comment1')");
    }

    @Test
    void getCommentsByPostId_shouldReturnAllCommentsByPostId() {
        List<Comment> comments = commentRepository.getCommentsByPostId(1L);

        assertNotNull(comments);
        assertEquals(2, comments.size());
        assertEquals("Comment1", comments.get(0).getContent());
        assertEquals("Comment2", comments.get(1).getContent());
    }

    @Test
    void deleteAllCommentsByPostId_shouldDeleteAllCommentsByPostId() {
        commentRepository.deleteAllCommentsByPostId(1L);

        List<Comment> comments = commentRepository.getCommentsByPostId(1L);

        assertNotNull(comments);
        assertEquals(0, comments.size());
    }

    @Test
    void deleteCommentById_shouldDeleteCommentById() {
        commentRepository.deleteCommentById(1L);

        List<Comment> comments = commentRepository.getCommentsByPostId(1L);

        assertNotNull(comments);
        assertEquals(1, comments.size());
    }

    @Test
    void saveComment_shouldSaveComment() {
        Comment comment = new Comment(null, 2L, "New comment");
        commentRepository.saveComment(comment);

        List<Comment> comments = commentRepository.getCommentsByPostId(2L);

        assertNotNull(comments);
        assertEquals(2, comments.size());
    }

    @Test
    void updateComment_shouldUpdateComment() {
        Comment comment = new Comment(1L, 1L, "Updated comment");
        commentRepository.updateComment(comment);

        List<Comment> updateComments = commentRepository.getCommentsByPostId(1L);

        assertNotNull(updateComments);
        assertEquals(comment.getContent(), updateComments.getFirst().getContent());
    }

    @Test
    void countByPostId_shouldReturnCountByPostId() {
        int count = commentRepository.countByPostId(1L);

        assertEquals(2, count);
    }
}
