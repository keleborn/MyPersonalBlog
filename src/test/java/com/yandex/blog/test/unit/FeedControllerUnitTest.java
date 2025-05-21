package com.yandex.blog.test.unit;

import com.yandex.blog.controllers.FeedController;
import com.yandex.blog.model.Post;
import com.yandex.blog.services.FeedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FeedControllerUnitTest {

    @Mock
    private FeedService feedService;

    @Mock
    private Model model;

    @InjectMocks
    private FeedController feedController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getPosts_shouldAddPostsAndReturnView() {
        List<Post> posts = new ArrayList<>();
        posts.add(new Post(1L, "title1", "shortDesc1", "content1", 0, null));
        posts.add(new Post(2L, "title2", "shortDesc2", "content2", 0, null));

        when(feedService.countPosts(eq(List.of("tag")))).thenReturn(posts.size());
        when(feedService.findAllByTagsWithPagination(eq(5), eq(1), eq(List.of("tag")) )).thenReturn(posts);
        String viewName = feedController.getPosts(5, 1, "tag", model);

        assertEquals("feed", viewName);
        verify(model, times(1)).addAttribute("posts", posts);
        verify(model, times(1)).addAttribute("totalPages", 1);
        verify(model, times(1)).addAttribute("currentPage", 1);
        verify(model, times(1)).addAttribute("limit", 5);
        verify(model, times(1)).addAttribute("selectedTags", "tag");
        verify(feedService, times(1)).findAllByTagsWithPagination(eq(5), eq(1), eq(List.of("tag")));
    }
}
