package com.yandex.blog.test.unit;

import com.yandex.blog.repository.JdbcNativeTagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TagRepositoryUnitTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private JdbcNativeTagRepository jdbcNativeTagRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllTagsByPostId_shouldExecuteQueryWithPostIdFilter() {
        List<String> tags = Arrays.asList("tag1", "tag2", "tag3");

        when(jdbcTemplate.query(anyString(), any(Object[].class), any(RowMapper.class))).thenReturn(tags);

        List<String> result = jdbcNativeTagRepository.findAllTagsByPostId(1L);

        assertEquals(3, result.size());
        verify(jdbcTemplate, times(1)).query(
                eq("select id, name from tags t join post_tag_links pt on t.id = pt.tag_id where pt.post_id = ?"),
                any(Object[].class),
                any(RowMapper.class)
        );
    }

    @Test
    void findTagByName_shouldExecuteQueryWithNameFilterAndReturnName() {
        List<String> tags = List.of("tag1");

        when(jdbcTemplate.query(anyString(), any(Object[].class), any(RowMapper.class))).thenReturn(tags);

        List<String> result = jdbcNativeTagRepository.findTagByName("tag1");

        assertEquals(1, result.size());
        verify(jdbcTemplate, times(1)).query(
                eq("select name from tags where name = ?"),
                any(Object[].class),
                any(RowMapper.class)
        );
    }

    @Test
    void findTagIdsByNames_shouldExecuteQueryWithMultipleNameFilterAndReturnIds() {
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<Long> tagIds = Arrays.asList(1L, 2L);

        when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(tagIds);

        List<Long> result = jdbcNativeTagRepository.findTagIdsByNames(tags);

        assertEquals(2, result.size());
        verify(jdbcTemplate, times(1)).query(
                eq("select id from tags where name in ('tag1','tag2')"),
                any(RowMapper.class)
        );
    }

    @Test
    void save_shouldExecuteInsert() {
        when(jdbcTemplate.update(anyString(), any(String.class))).thenReturn(1);

        jdbcNativeTagRepository.save("tag1");

        verify(jdbcTemplate, times(1)).update(eq("insert into tags (name) values (?)"), any(Object.class));
    }

    @Test
    void delete_shouldExecuteDelete() {
        when(jdbcTemplate.update(anyString(), any(Long.class))).thenReturn(1);

        jdbcNativeTagRepository.deleteTagsForPost(1L);

        verify(jdbcTemplate, times(1)).update(eq("delete from post_tag_links where post_id = ?"), any(Object.class));
    }

    @Test
    void attachTags_shouldExecuteInsertIntoLinksTable() {
        when(jdbcTemplate.update(anyString(), any(Object.class))).thenReturn(1);

        jdbcNativeTagRepository.attachTags(1L, List.of(1L, 2L));

        verify(jdbcTemplate, times(2)).update(eq("insert into post_tag_links (post_id, tag_id) values (?, ?)"),
                any(Long.class),
                any(Long.class)
        );
    }
}
