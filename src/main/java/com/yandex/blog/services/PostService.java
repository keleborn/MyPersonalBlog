package com.yandex.blog.services;

import com.yandex.blog.model.Comment;
import com.yandex.blog.model.Post;
import com.yandex.blog.model.Tag;
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
import java.util.Optional;
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
        Optional<Post> post = postRepository.findById(id);
        post.ifPresent(value -> value.setTags(tagRepository.findAllTagsByPostId(id)));
        return post.orElse(null);
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
            Optional<Post> postOptional = postRepository.findById(post.getId());
            postOptional.ifPresent(value -> post.setImageUrl(value.getImageUrl()));
        }
        postRepository.update(post.getId(), post.getTitle(), post.getShortDescription(), post.getContent(), post.getImageUrl());
    }

    public void deleteCommentByCommentId(Long commentId) {
        commentRepository.deleteCommentById(commentId);
    }

    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public void updateComment(Long commentId, String content) {
        commentRepository.updateContentById(commentId, content);
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
        postRepository.incrementLikes(post.getId());
    }

    public void saveTags(long postId, List<String> tags) {
        tagRepository.deleteTagsForPost(postId);
        tags.forEach(this::createTagIfNotExists);
        for (long tag : tagRepository.findTagIdsByNames(tags)) {
            tagRepository.attachTag(postId, tag);
        }
    }

    private void createTagIfNotExists(String tag) {
        String tagInDB = tagRepository.findTagByName(tag);
        if (tagInDB == null || tagInDB.isEmpty()) {
            tagRepository.save(new Tag(null, tag));
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
