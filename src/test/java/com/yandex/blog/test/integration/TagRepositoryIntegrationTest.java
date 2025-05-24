package com.yandex.blog.test.integration;

import com.yandex.blog.model.Tag;
import com.yandex.blog.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TagRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest{

    @Autowired
    private TagRepository tagRepository;

    @BeforeEach
    public void setUp() {
        tagRepository.save(new Tag(null, "tag1"));
        tagRepository.save(new Tag(null, "tag2"));
        tagRepository.save(new Tag(null, "tag3"));

        tagRepository.attachTag(1L, 1L);
        tagRepository.attachTag(1L, 2L);
    }

    @Test
    void save_shouldSaveTag() {
        String savedTag = tagRepository.findTagByName("tag1");

        assertThat(savedTag).isNotNull();
        assertThat(savedTag).isEqualTo("tag1");
    }

    @Test
    void findAllTagsByPostId_shouldReturnAllTagsByPostId() {
        List<String> tags = tagRepository.findAllTagsByPostId(1L);

        assertThat(tags).isNotEmpty();
        assertThat(tags.size()).isEqualTo(2);
        assertThat(tags.getFirst()).isEqualTo("tag1");
    }

    @Test
    void findTagByName_shouldReturnTagByName() {
        String tag = tagRepository.findTagByName("tag2");

        assertThat(tag).isNotNull();
    }

    @Test
    void findTagIdsByNames_shouldReturnTagIdsByNames() {
        List<Long> tagIds = tagRepository.findTagIdsByNames(List.of("tag1", "tag2"));

        assertThat(tagIds).isNotEmpty();
        assertThat(tagIds.size()).isEqualTo(2);
        assertThat(tagIds.get(0)).isEqualTo(1L);
        assertThat(tagIds.get(1)).isEqualTo(2L);
    }

    @Test
    void deleteTagsForPost_shouldDeleteTagLinksForPostId() {
        tagRepository.deleteTagsForPost(1L);

        List<String> tagsForPost = tagRepository.findAllTagsByPostId(1L);
        String tag = tagRepository.findTagByName("tag1");

        assertThat(tagsForPost).isEmpty();
        assertThat(tag).isNotNull();
        assertThat(tag).isEqualTo("tag1");
    }

    @Test
    void attachTags_shouldCreatePostTagLink() {
        tagRepository.attachTag(1L, 3L);

        List<String> tagsForPost = tagRepository.findAllTagsByPostId(1L);
        assertThat(tagsForPost).isNotEmpty();
        assertThat(tagsForPost.size()).isEqualTo(3);
    }
}
