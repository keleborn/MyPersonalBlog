package com.yandex.blog.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("TAGS")
public class Tag {
    @Id
    private Long id;
    private String name;

    public Tag() {

    }

    public Tag(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
