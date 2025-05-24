package com.yandex.blog.test.integration;

import com.yandex.blog.model.Post;
import com.yandex.blog.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class PostRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    public void setUp() {
        postRepository.save(new Post(null, "Title1", "ShorDesc1", "Content1", 0, null));
        postRepository.save(new Post(null, "Title2", "ShorDesc2", "Content2", 0, null));
        postRepository.save(new Post(null, "Title3", "ShorDesc3", "Content3", 0, null));
    }

    @Test
    void save_shouldAddPostToRepository() {
        Optional<Post> savedPost = postRepository.findById(1L);

        assertThat(savedPost).isPresent();
        assertThat(savedPost.get().getId()).isEqualTo(1L);
        assertThat(savedPost.get().getTitle()).isEqualTo("Title1");
        assertThat(savedPost.get().getContent()).isEqualTo("Content1");
        assertThat(savedPost.get().getShortDescription()).isEqualTo("ShorDesc1");
    }

    @Test
    void findAll_shouldReturnAllPosts() {
        List<Post> posts = postRepository.findAll();

        assertThat(posts).isNotEmpty();
        assertThat(posts.size()).isEqualTo(3);
        assertThat(posts.get(0).getTitle()).isEqualTo("Title1");
        assertThat(posts.get(1).getTitle()).isEqualTo("Title2");
        assertThat(posts.get(2).getTitle()).isEqualTo("Title3");
    }

    @Test
    void findAllWithPagination_shouldReturnLimitedNumberOfPostsWithOffset() {
        List<Post> page1 = postRepository.findAllWithPagination(3, 3);
        List<Post> page2 = postRepository.findAllWithPagination(3, 0);
        List<Post> page3 = postRepository.findAllWithPagination(2, 1);

        assertThat(page1).isEmpty();
        assertThat(page2.size()).isEqualTo(3);
        assertThat(page3.size()).isEqualTo(2);

        assertThat(page2.getFirst().getTitle()).isEqualTo("Title1");
        assertThat(page3.getFirst().getTitle()).isEqualTo("Title2");
    }

    @Test
    void findAllWithPagination_withTags_shouldReturnLimitedNumberOfPostsWithOffsetFilteredByTags() {
        List<Post> page = postRepository.findAllWithPagination(3, 0, new ArrayList<>());

        assertThat(page).isEmpty();
    }

    @Test
    void deleteById_shouldRemovePostFromDatabase() {
        postRepository.deleteById(1L);

        Optional<Post> post = postRepository.findById(1L);

        assertThat(post).isNotPresent();
    }

    @Test
    void countPosts_shouldReturnNumberOfPostsInDatabase() {
        int count = postRepository.countPosts();

        assertThat(count).isEqualTo(3);
    }

    @Test
    void countPosts_withTags_shouldReturnNumberOfPostsFilteredByTagsInDatabase() {
        int count = postRepository.countPosts(new ArrayList<>());

        assertThat(count).isEqualTo(0);
    }

    @Test
    void update_shouldUpdatePost() {
        postRepository.update(2L, "Title2Updated", "ShorDesc2Updated", "Content2Updated", "UrlUpdated");

        Optional<Post> post = postRepository.findById(2L);

        assertThat(post).isPresent();
        assertThat(post.get().getTitle()).isEqualTo("Title2Updated");
        assertThat(post.get().getShortDescription()).isEqualTo("ShorDesc2Updated");
        assertThat(post.get().getContent()).isEqualTo("Content2Updated");
        assertThat(post.get().getImageUrl()).isEqualTo("UrlUpdated");
    }
}
