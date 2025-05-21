package com.yandex.blog.controllers;

import com.yandex.blog.model.Post;
import com.yandex.blog.services.FeedService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.yandex.blog.utils.PostUtils.parseTags;

@Controller
@RequestMapping("/feed")
public class FeedController {

    private final FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping
    public String getPosts(@RequestParam(name = "limit", defaultValue = "5") int limit,
                           @RequestParam(name = "page", defaultValue = "1") int page,
                           @RequestParam(name = "tags", required = false) String tags,
                           Model model) {
        List<String> tagList = parseTags(tags);
        List<Post> posts = feedService.findAllByTagsWithPagination(limit, page, tagList);

        int totalPosts = feedService.countPosts(tagList);
        int totalPages = (int) Math.ceil((double) totalPosts / limit);

        model.addAttribute("posts", posts);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("limit", limit);
        model.addAttribute("selectedTags", tags);
        return "feed";
    }
}
