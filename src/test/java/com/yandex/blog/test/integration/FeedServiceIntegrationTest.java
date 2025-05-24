package com.yandex.blog.test.integration;

import com.yandex.blog.model.Post;
import com.yandex.blog.model.Tag;
import com.yandex.blog.repository.PostRepository;
import com.yandex.blog.repository.TagRepository;
import com.yandex.blog.services.FeedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class FeedServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private FeedService feedService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @BeforeEach
    public void setUp() {
        Post post1 = postRepository.save(new Post(null, "Post1", "ShortDesc1", "Content1", 0, null));
        Post post2 = postRepository.save(new Post(null, "Post2", "ShortDesc2", "Content2", 0, null));

        Tag savedTag = tagRepository.save(new Tag(null, "tag"));
        tagRepository.attachTag(post1.getId(), savedTag.getId());
    }

    @Test
    void findAll_shouldReturnAllPosts() {
        List<Post> posts = feedService.findAll();
        assertThat(posts).hasSize(2);
    }

    @Test
    void findAllByTagsWithPagination_shouldReturnAllPosts() {
        List<Post> page1 = feedService.findAllByTagsWithPagination(5, 1, List.of());
        List<Post> page2 = feedService.findAllByTagsWithPagination(5, 2, List.of());

        assertThat(page1).hasSize(2);
        assertThat(page2).hasSize(0);
    }

    @Test
    void findAllByTagsWithPagination_withTags_shouldReturnAllPostsFilteredByTags() {
        List<Post> page1 = feedService.findAllByTagsWithPagination(5, 1, List.of("tag"));
        List<Post> page2 = feedService.findAllByTagsWithPagination(5, 2, List.of("tag"));

        assertThat(page1).hasSize(1);
        assertThat(page2).hasSize(0);
    }

    @Test
    void countPosts_shouldReturnCount() {
        int count = feedService.countPosts(List.of());

        assertThat(count).isEqualTo(2);
    }

    @Test
    void countPosts_withTags_shouldReturnCountFilteredByTags() {
        int count = feedService.countPosts(List.of("tag"));

        assertThat(count).isEqualTo(1);
    }
}
