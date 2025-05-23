package com.yandex.blog.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Post {
    private Long id;
    private String title;
    private String shortDescription;
    private String content;
    private String imageUrl;
    private int likes;
    private List<String> tags = new ArrayList<>();
    private List<Comment> comments = new ArrayList<>();

    public Post() {

    }

    public Post(Long id, String title, String shortDescription, String content, int likes, String imageUrl) {
        this.id = id;
        this.title = title;
        this.shortDescription = shortDescription;
        this.content = content;
        this.likes = likes;
        this.imageUrl = imageUrl;
    }
}
