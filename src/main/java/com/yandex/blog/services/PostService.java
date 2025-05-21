package com.yandex.blog.services;

import com.yandex.blog.model.Post;
import com.yandex.blog.repository.PostRepository;
import com.yandex.blog.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.yandex.blog.utils.PostUtils.splitBySentences;
import static com.yandex.blog.utils.PostUtils.splitBySymbol;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    public PostService(PostRepository postRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
    }

    public Post findById(Long id) {
        Post post = postRepository.findById(id);
        if (post != null) {
            post.setTags(tagRepository.findAllTagsByPostId(id));
        }
        return post;
    }

    public void save(Post post) {
        postRepository.save(post);
    }

    public void update(Post post) {
        postRepository.update(post);
    }

    public void deleteById(Long id) {
        postRepository.deleteById(id);
        tagRepository.deleteTagsForPost(id);
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
}
