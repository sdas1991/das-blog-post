package com.das.blogservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "posts")
public class Post {
    @Id
    private String id;
    private String title;
    private String slug;
    private String excerpt;
    private String content;
    private Author author;
    private List<String> tags;
    private String category;
    private Boolean published;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer views;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Author {
        private Integer id;
        private String name;
        private String email;
    }
}
