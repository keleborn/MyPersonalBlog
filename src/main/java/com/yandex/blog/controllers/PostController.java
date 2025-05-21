package com.yandex.blog.controllers;

import com.yandex.blog.model.Post;
import com.yandex.blog.services.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.yandex.blog.utils.PostUtils.parseTags;

@Controller
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }
    
    @GetMapping("/{id}")
    public String getPost(@PathVariable("id") Long id, Model model) {
        Post post = postService.findById(id);
        List<String> paragraphs = postService.splitContentToParagraphs(post.getContent());
        model.addAttribute("post", post);
        model.addAttribute("paragraphs", paragraphs);
        return "post";
    }

    @GetMapping("/new")
    public String showCreatePost(Model model) {
        model.addAttribute("post", new Post());
        return "createPost";
    }

    @PostMapping("/new")
    public String createPost(@ModelAttribute("post") Post post, @RequestParam(name = "tags", required = false) String tags) {
        postService.save(post);
        postService.saveTags(post.getId(), parseTags(tags));

        return "redirect:/feed";
    }

    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable("id") Long id) {
        postService.deleteById(id);
        return "redirect:/feed";
    }

    @GetMapping("/edit/{id}")
    public String showEditPostForm(@PathVariable("id") Long id, Model model) {
        Post post = postService.findById(id);
        model.addAttribute("post", post);
        return "editPost";
    }

    @PostMapping("/edit/{id}")
    public String editPost(@PathVariable("id") Long id, @ModelAttribute("post") Post post, @RequestParam(name = "tags", required = false) String tags) {
        post.setId(id);
        post.setTags(parseTags(tags));
        postService.update(post);
        postService.saveTags(post.getId(), post.getTags());
        return "redirect:/post/" + id;
    }

    @PostMapping("/like/{id}")
    public String likePost(@PathVariable("id") Long id, @ModelAttribute("post") Post post) {
        postService.incrementLikes(post);
        return "redirect:/post/" + id;
    }
}
