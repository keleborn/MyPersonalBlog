package com.yandex.blog.test.integration;

import com.yandex.blog.configuration.DataSourceConfiguration;
import com.yandex.blog.model.Post;
import com.yandex.blog.repository.JdbcNativePostRepository;
import com.yandex.blog.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


@SpringJUnitConfig(classes = {DataSourceConfiguration.class, JdbcNativePostRepository.class})
@TestPropertySource(locations = "classpath:test-application.properties")
public class PostRepositoryIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.execute("DELETE FROM posts");
        jdbcTemplate.execute("alter table posts alter column id restart with 1");
        jdbcTemplate.execute("insert into posts(title, shortDescription, content) values ('Title1', 'ShorDesc1', 'Content1')");
        jdbcTemplate.execute("insert into posts(title, shortDescription, content) values ('Title2', 'ShorDesc2', 'Content2')");
        jdbcTemplate.execute("insert into posts(title, shortDescription, content) values ('Title3', 'ShorDesc3', 'Content3')");
    }

    @Test
    void save_shouldAddPostToRepository() {
        Post post = new Post();
        post.setTitle("Test Post");
        post.setContent("Test Content");
        post.setShortDescription("Test ShortDescription");

        postRepository.save(post);
        Post savedPost = postRepository.findAll().stream()
                .filter(createdPost -> createdPost.getId().equals(4L))
                .findFirst()
                .orElse(null);

        assertNotNull(savedPost);
        assertEquals(4L, savedPost.getId());
        assertEquals(post.getTitle(), savedPost.getTitle());
        assertEquals(post.getContent(), savedPost.getContent());
        assertEquals(post.getShortDescription(), savedPost.getShortDescription());
    }

    @Test
    void findAll_shouldReturnAllPosts() {
        List<Post> posts = postRepository.findAll();

        assertNotNull(posts);
        assertEquals(3, posts.size());

        Post post = posts.getFirst();
        assertEquals(1L, post.getId());
        assertEquals("Title1", post.getTitle());
    }

    @Test
    void findAllWithPagination_shouldReturnLimitedNumberOfPostsWithOffset() {
        List<Post> page1 = postRepository.findAllWithPagination(3, 3, new ArrayList<>());
        List<Post> page2 = postRepository.findAllWithPagination(3, 0, new ArrayList<>());
        List<Post> page3 = postRepository.findAllWithPagination(2, 1, new ArrayList<>());

        assertEquals(0, page1.size());
        assertEquals(3, page2.size());
        assertEquals(2, page3.size());
        assertEquals("Title1", page2.getFirst().getTitle());
        assertEquals("Title2", page3.getFirst().getTitle());
    }

    @Test
    void findById_shouldReturnPost() {
        Post post = postRepository.findById(2L);

        assertNotNull(post);

        assertEquals(2L, post.getId());
        assertEquals("Title2", post.getTitle());
        assertEquals("ShorDesc2", post.getShortDescription());
        assertEquals("Content2", post.getContent());
    }

    @Test
    void deleteById_shouldRemovePostFromDatabase() {
        postRepository.deleteById(1L);

        List<Post> posts = postRepository.findAll();

        Post deletedPost = posts.stream()
                .filter(createdPost -> createdPost.getId().equals(1L))
                .findFirst()
                .orElse(null);
        assertNull(deletedPost);
    }

    @Test
    void countPosts_shouldReturnNumberOfPostsInDatabase() {
        int count = postRepository.countPosts(new ArrayList<>());
        assertEquals(3, count);
    }

    @Test
    void update_shouldUpdatePost() {
        Post post = new Post(2L, "Title2Updated", "ShorDesc2Updated", "Content2Updated", 0, "testPathUrl");
        postRepository.update(post);

        Post updatedPost = postRepository.findById(2L);
        assertNotNull(updatedPost);
        assertEquals("Title2Updated", updatedPost.getTitle());
        assertEquals("ShorDesc2Updated", updatedPost.getShortDescription());
        assertEquals("Content2Updated", updatedPost.getContent());
        assertEquals("testPathUrl", updatedPost.getImageUrl());
    }

    @Test
    void incrementLikes_shouldIncrementLikes() {
        Post post = postRepository.findById(2L);
        postRepository.incrementLikes(post);

        Post updatedPost = postRepository.findById(2L);
        assertNotNull(updatedPost);
        assertEquals(1, updatedPost.getLikes());
    }
}
