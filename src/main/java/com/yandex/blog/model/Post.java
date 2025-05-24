package com.yandex.blog.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Table("POSTS")
public class Post {
    @Id
    private Long id;
    private String title;
    private String shortDescription;
    private String content;
    private String imageUrl;
    private int likes;
    @MappedCollection(idColumn = "POST_ID")
    private Set<Comment> comments = new HashSet<>();
    @Transient
    private List<String> tags = new ArrayList<>();

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
