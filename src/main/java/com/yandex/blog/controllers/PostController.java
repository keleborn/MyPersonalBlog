package com.yandex.blog.controllers;

import com.yandex.blog.model.Comment;
import com.yandex.blog.model.Post;
import com.yandex.blog.services.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public String createPost(@ModelAttribute("post") Post post,
                             @RequestParam(name = "image", required = false) MultipartFile image,
                             @RequestParam(name = "tags", required = false) String tags) {
        postService.save(post, image);
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
    public String editPost(@PathVariable("id") Long id,
                           @ModelAttribute("post") Post post,
                           @RequestParam(name = "tags", required = false) String tags,
                           @RequestParam(value = "image", required = false) MultipartFile image,
                           @RequestParam(value = "deleteImage", required = false, defaultValue = "false") boolean deleteImage) {
        post.setId(id);
        post.setTags(parseTags(tags));
        postService.update(post, image, deleteImage);
        postService.saveTags(post.getId(), post.getTags());
        return "redirect:/post/" + id;
    }

    @PostMapping("/like/{id}")
    public String likePost(@PathVariable("id") Long id, @ModelAttribute("post") Post post) {
        postService.incrementLikes(post);
        return "redirect:/post/" + id;
    }

    @PostMapping("/comments")
    @ResponseBody
    public ResponseEntity<Long> addComment(@RequestParam("postId") long postId,
                                           @RequestParam("content") String content) {
        Comment saved = postService.saveComment(new Comment(null, postId, content));
        return ResponseEntity.ok(saved.getId());
    }

    @PostMapping("/comments/edit")
    @ResponseBody
    public ResponseEntity<Void> editComment(@RequestParam("id") long id,
                                            @RequestParam("content") String content) {
        postService.updateComment(id, content);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comments/delete")
    @ResponseBody
    public ResponseEntity<Void> deleteComment(@RequestParam("id") long id) {
        postService.deleteCommentByCommentId(id);
        return ResponseEntity.ok().build();
    }
}
