package com.yandex.blog.test.unit;

import com.yandex.blog.model.Post;
import com.yandex.blog.repository.JdbcNativePostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.KeyHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JdbcNativePostRepositoryUnitTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private JdbcNativePostRepository jdbcNativePostRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_shouldReturnAllPosts() {
        List<Post> posts = new ArrayList<>();
        posts.add(new Post(1L, "title1", "desc1", "content1", 0));
        posts.add(new Post(2L, "title2", "desc2", "content2", 0));
        posts.add(new Post(3L, "title3", "desc3", "content3", 0));

        when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(posts);

        List<Post> result = jdbcNativePostRepository.findAll();

        assertEquals(3, result.size());
        verify(jdbcTemplate, times(1)).query(anyString(), any(RowMapper.class));
    }

    @Test
    void findAllWithPagination_shouldReturnLimitedNumberOfPostsWithOffset() {
        List<Post> posts = new ArrayList<>();
        posts.add(new Post(2L, "title2", "desc2", "content2", 0));
        posts.add(new Post(3L, "title3", "desc3", "content3", 0));
        posts.add(new Post(4L, "title4", "desc4", "content4", 0));
        posts.add(new Post(5L, "title5", "desc5", "content5", 0));
        posts.add(new Post(6L, "title6", "desc6", "content6", 0));

        when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(posts);

        List<Post> result = jdbcNativePostRepository.findAllWithPagination(5, 1, new ArrayList<>());
        assertEquals(5, result.size());

        verify(jdbcTemplate, times(1)).query(anyString(), any(RowMapper.class));
    }

    @Test
    void findById_shouldReturnPost() {
        Post post = new Post(1L, "title1", "desc1", "content1", 0);

        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), any(RowMapper.class))).thenReturn(post);

        Post result = jdbcNativePostRepository.findById(post.getId());

        assertNotNull(result);
        assertEquals(post, result);
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), any(Object[].class), any(RowMapper.class));
    }

    @Test
    void save_shouldExecuteInsertQueryAndSetPostId() {
        Post post = new Post(1L, "title1", "desc1", "content1", 0);

        when(jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class))).thenAnswer(
                invocationOnMock -> {
                    KeyHolder keyHolder = invocationOnMock.getArgument(1);
                    Map<String, Object> keys = new HashMap<>();
                    keys.put("id", 2L);
                    keyHolder.getKeyList().add(keys);
                    return 1;
                }
        );

        jdbcNativePostRepository.save(post);

        assertEquals(2L, post.getId());
        verify(jdbcTemplate, times(1)).update(any(PreparedStatementCreator.class), any(KeyHolder.class));
    }

    @Test
    void deleteById_shouldExecuteDeleteQuery() {
        jdbcNativePostRepository.deleteById(1L);
        verify(jdbcTemplate, times(1)).update(eq("delete from posts where id = ?"), eq(1L));
    }

    @Test
    void count_withoutTags_shouldExecuteCountQuery() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(1);

        jdbcNativePostRepository.countPosts(new ArrayList<>());

        verify(jdbcTemplate, times(1)).queryForObject("select count(*) from posts p ", Integer.class);
    }

    @Test
    void count_withTags_shouldExecuteCountQuery() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(1);

        jdbcNativePostRepository.countPosts(List.of(1L, 2L));

        verify(jdbcTemplate, times(1)).queryForObject(
                "select count(*) from posts p left join post_tag_links pt on p.id = pt.post_id where pt.tag_id in (1, 2)",
                Integer.class
        );
    }

    @Test
    void update_shouldExecuteUpdateQuery() {
        Post post = new Post(1L, "title1", "desc1", "content1", 0);
        when(jdbcTemplate.update(anyString(), any(Object[].class))).thenReturn(1);

        jdbcNativePostRepository.update(post);

        verify(jdbcTemplate, times(1)).update(eq("update posts set title = ?, shortDescription = ?, content = ? where id = ?"),
                eq(post.getTitle()), eq(post.getShortDescription()), eq(post.getContent()), eq(post.getId()));
    }

    @Test
    void incrementLikes_shouldExecuteUpdateQuery() {
        Post post = new Post(1L, "title1", "desc1", "content1", 0);
        when(jdbcTemplate.update(anyString(), any(Object[].class))).thenReturn(1);

        jdbcNativePostRepository.incrementLikes(post);

        verify(jdbcTemplate, times(1)).update(eq("update posts set likes = likes + 1 where id = ?"), eq(post.getId()));
    }
}
