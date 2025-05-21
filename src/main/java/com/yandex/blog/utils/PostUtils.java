package com.yandex.blog.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostUtils {
    public static List<String> splitBySentences(String content, List<String> paragraphs) {
        String[] sentences = content.split("(?<=[.!?])\\s+");
        StringBuilder paragraph = new StringBuilder();
        int count = 0;
        for (String sentence : sentences) {
            paragraph.append(sentence).append(" ");
            count++;
            if (count == 3) {
                paragraphs.add(paragraph.toString().trim());
                paragraph.setLength(0);
                count = 0;
            }
        }

        if (!paragraph.isEmpty()) {
            paragraphs.add(paragraph.toString().trim());
        }
        return paragraphs;
    }

    public static List<String> splitBySymbol(String content, List<String> paragraphs) {
        String[] lines = content.split("\n");
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                paragraphs.add(line.trim());
            }
        }
        return paragraphs;
    }

    public static String getTagsAsString(List<String> tags) {
        return String.join(", ", tags);
    }

    public static List<String> parseTags(String tags) {
        if (tags == null || tags.isBlank()) {
            return new ArrayList<>();
        }
        return Arrays.stream(tags.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }
}
