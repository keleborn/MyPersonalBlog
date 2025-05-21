package com.yandex.blog.repository;

import com.yandex.blog.model.Comment;

import java.util.List;

public interface CommentRepository {
    List<Comment> getCommentsByPostId(Long postId);

    void deleteAllCommentsByPostId(Long postId);

    void deleteCommentById(Long id);

    void saveComment(Comment comment);

    void updateComment(Comment comment);

    int countByPostId(Long postId);
}
