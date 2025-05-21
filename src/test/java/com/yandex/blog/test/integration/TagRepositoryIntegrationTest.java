package com.yandex.blog.test.integration;

import com.yandex.blog.configuration.DataSourceConfiguration;
import com.yandex.blog.repository.JdbcNativeTagRepository;
import com.yandex.blog.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, JdbcNativeTagRepository.class})
@TestPropertySource(locations = "classpath:test-application.properties")
public class TagRepositoryIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TagRepository tagRepository;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.execute("DELETE FROM tags");
        jdbcTemplate.execute("DELETE FROM post_tag_links");
        jdbcTemplate.execute("alter table tags alter column id restart with 1");

        jdbcTemplate.execute("insert into tags(name) values ('tag1')");
        jdbcTemplate.execute("insert into tags(name) values ('tag2')");
        jdbcTemplate.execute("insert into tags(name) values ('tag3')");

        jdbcTemplate.execute("insert into post_tag_links(post_id, tag_id) values (1, 1)");
        jdbcTemplate.execute("insert into post_tag_links(post_id, tag_id) values (1, 2)");
    }

    @Test
    void findAllTagsByPostId_shouldReturnAllTagsByPostId() {
        List<String> tags = tagRepository.findAllTagsByPostId(1L);

        assertNotNull(tags);
        assertEquals(2, tags.size());
        assertEquals("tag1", tags.get(0));
        assertEquals("tag2", tags.get(1));
    }

    @Test
    void findTagByName_shouldReturnTagByName() {
        List<String> tags = tagRepository.findTagByName("tag1");

        assertNotNull(tags);
        assertEquals("tag1", tags.getFirst());
    }

    @Test
    void findTagByName_shouldReturnEmptyListIfTagDoesNotExist() {
        List<String> tags = tagRepository.findTagByName("tag10");

        assertNotNull(tags);
        assertEquals(0, tags.size());
    }

    @Test
    void findTagIdsByNames_shouldReturnTagIdsByNames() {
        List<Long> tagIds = tagRepository.findTagIdsByNames(List.of("tag1", "tag2"));

        assertNotNull(tagIds);
        assertEquals(2, tagIds.size());
        assertEquals(1L, tagIds.get(0));
        assertEquals(2L, tagIds.get(1));
    }

    @Test
    void save_shouldSaveTag() {
        tagRepository.save("tag4");

        List<String> savedTag = tagRepository.findTagByName("tag4");

        assertNotNull(savedTag);
        assertEquals(1, savedTag.size());
        assertEquals("tag4", savedTag.get(0));
    }

    @Test
    void deleteTagsForPost_shouldDeleteAllLinksByPostId() {
        tagRepository.deleteTagsForPost(1L);

        List<String> result = tagRepository.findAllTagsByPostId(1L);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void attachTags_shouldCreateLinksBetweenTagIdAndPostId() {
        tagRepository.attachTags(2L, List.of(2L, 3L));

        List<String> result = tagRepository.findAllTagsByPostId(2L);
        assertNotNull(result);
        assertEquals(2, result.size());
    }
}
