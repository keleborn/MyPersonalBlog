package com.yandex.blog.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Comment {
    private Long id;
    private Long postId;
    private String content;

    public Comment() {

    }

    public Comment(Long id, Long postId, String content) {
        this.id = id;
        this.postId = postId;
        this.content = content;
    }
}
