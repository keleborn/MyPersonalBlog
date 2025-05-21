package com.yandex.blog.test.unit;

import com.yandex.blog.model.Post;
import com.yandex.blog.repository.PostRepository;
import com.yandex.blog.repository.TagRepository;
import com.yandex.blog.services.FeedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FeedServiceUnitTest {
    private Post post1;
    private Post post2;

    @Mock
    private PostRepository postRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private FeedService feedService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        post1 = new Post(1L, "title1", "desc1", "content1", 0, null);
        post2 = new Post(2L, "title2", "desc2", "content2", 0, null);
    }

    @Test
    void findAll_shouldReturnAllPosts() {
        when(postRepository.findAll()).thenReturn(Arrays.asList(post1, post2));

        List<Post> result = feedService.findAll();
        assertEquals(2, result.size());
        verify(postRepository, times(1)).findAll();
    }

    @Test
    void findAllWithPagination_shouldReturnLimitedNumberOfPosts() {
        when(postRepository.findAllWithPagination(1,0, new ArrayList<>())).thenReturn(Collections.singletonList(post2));

        List<Post> result = feedService.findAllByTagsWithPagination(1, 1, List.of("tag"));
        assertEquals(1, result.size());
        verify(postRepository, times(1)).findAllWithPagination(1, 0, new ArrayList<>());
    }
}
