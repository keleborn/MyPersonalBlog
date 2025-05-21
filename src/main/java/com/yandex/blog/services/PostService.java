package com.yandex.blog.services;

import com.yandex.blog.model.Comment;
import com.yandex.blog.model.Post;
import com.yandex.blog.repository.CommentRepository;
import com.yandex.blog.repository.PostRepository;
import com.yandex.blog.repository.TagRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.yandex.blog.utils.PostUtils.splitBySentences;
import static com.yandex.blog.utils.PostUtils.splitBySymbol;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;

    @Value("${image.upload.dir}")
    private String imagesPath;

    public PostService(PostRepository postRepository, TagRepository tagRepository , CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.commentRepository = commentRepository;
    }

    public Post findById(Long id) {
        Post post = postRepository.findById(id);
        if (post != null) {
            post.setTags(tagRepository.findAllTagsByPostId(id));
            post.setComments(commentRepository.getCommentsByPostId(id));
        }
        return post;
    }

    public void save(Post post, MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path path = Paths.get(imagesPath, fileName);
            try {
                Files.createDirectories(path.getParent());
                Files.write(path, image.getBytes());
                post.setImageUrl(fileName);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка при сохранени", e);
            }
        }
        postRepository.save(post);
    }

    public void update(Post post, MultipartFile image, boolean shouldDeleteImage) {
        if (shouldDeleteImage) {
            deleteImage(post.getImageUrl());
            post.setImageUrl(null);
        } else if (image != null && !image.isEmpty()) {
            deleteImage(post.getImageUrl());
            save(post, image);
        } else {
            String existingImageUrl = postRepository.findById(post.getId()).getImageUrl();
            post.setImageUrl(existingImageUrl);
        }
        postRepository.update(post);
    }

    public void deleteCommentByCommentId(Post post, Long commentId) {
        //post.getComments().;
        commentRepository.deleteCommentById(commentId);
    }

    public void saveComment(Comment comment) {
        commentRepository.saveComment(comment);
    }

    public void deleteById(Long id) {
        postRepository.deleteById(id);
        tagRepository.deleteTagsForPost(id);
        commentRepository.deleteAllCommentsByPostId(id);
    }

    public List<String> splitContentToParagraphs(String content) {
        List<String> paragraphs = new ArrayList<>();
        content = content.replace("\\n", "\n");
        if (content.contains("\n")) {
            return splitBySymbol(content, paragraphs);
        }
        return splitBySentences(content, paragraphs);
    }

    public void incrementLikes(Post post) {
        postRepository.incrementLikes(post);
    }

    public void saveTags(long postId, List<String> tags) {
        tagRepository.deleteTagsForPost(postId);
        tags.forEach(this::createTagIfNotExists);
        tagRepository.attachTags(postId, tagRepository.findTagIdsByNames(tags));
    }

    private void createTagIfNotExists(String tag) {
        List<String> tagInDB = tagRepository.findTagByName(tag);
        if (tagInDB.isEmpty()) {
            tagRepository.save(tag);
        }
    }

    private void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }
        Path path = Paths.get(imagesPath, imageUrl);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось удалить файл", e);
        }
    }
}
