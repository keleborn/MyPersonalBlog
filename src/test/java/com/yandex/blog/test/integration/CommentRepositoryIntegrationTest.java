package com.yandex.blog.test.integration;

import com.yandex.blog.model.Comment;
import com.yandex.blog.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.execute("insert into comments(post_id, content) values (1, 'Comment1')");
        jdbcTemplate.execute("insert into comments(post_id, content) values (1, 'Comment2')");
        jdbcTemplate.execute("insert into comments(post_id, content) values (2, 'Comment1')");
    }

    @Test
    void findByPostId_shouldReturnCommentsByPostId() {
        List<Comment> comments = commentRepository.findByPostId(1L);

        assertThat(comments).isNotEmpty();
        assertThat(comments.size()).isEqualTo(2);
        assertThat(comments.get(0).getContent()).isEqualTo("Comment1");
        assertThat(comments.get(1).getContent()).isEqualTo("Comment2");
    }

    @Test
    void deleteAllCommentsByPostId_shouldDeleteAllCommentsByPostId() {
        commentRepository.deleteAllCommentsByPostId(1L);

        List<Comment> comments = commentRepository.findByPostId(1L);

        assertThat(comments).isEmpty();
    }

    @Test
    void deleteCommentById_shouldDeleteCommentById() {
        commentRepository.deleteCommentById(1L);

        List<Comment> comments = commentRepository.findByPostId(1L);
        Optional<Comment> comment = commentRepository.findById(1L);

        assertThat(comment).isNotPresent();
        assertThat(comments).isNotEmpty();
        assertThat(comments.size()).isEqualTo(1);
    }

    @Test
    void save_shouldSaveComment() {
        Comment comment = new Comment(null, 2L, "New comment");
        Comment savedComment = commentRepository.save(comment);

        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getId()).isEqualTo(4L);
    }

    @Test
    void updateComment_shouldUpdateComment() {
        commentRepository.updateContentById(1L, "updated");

        Optional<Comment> comment = commentRepository.findById(1L);

        assertThat(comment).isPresent();
        assertThat(comment.get().getContent()).isEqualTo("updated");
    }

    @Test
    void countByPostId_shouldReturnCountByPostId() {
        int count = commentRepository.countByPostId(1L);

        assertThat(count).isEqualTo(2);
    }
}
