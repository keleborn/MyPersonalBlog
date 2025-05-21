package com.yandex.blog.utils;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

@Component
public class PageUtils {
    public List<Integer> getPages(int totalPages) {
        return IntStream.rangeClosed(1, totalPages).boxed().toList();
    }
}
