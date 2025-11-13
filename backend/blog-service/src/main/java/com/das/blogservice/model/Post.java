package com.das.blogservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.TextIndexed;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "posts")
public class Post {
    @Id
    private String id;

    @TextIndexed(weight = 3)
    private String title;

    private String slug;

    @TextIndexed(weight = 2)
    private String excerpt;

    @TextIndexed
    private String content;

    private Author author;

    @TextIndexed
    private List<String> tags;

    private String category;

    // Module system for organizing content (AI, Travel, Dev Tools, etc.)
    private String module;

    private Boolean published;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer views;

    // Weight for trending/priority (higher = more important)
    // Default: 1, Admin can set 1-10
    private Integer weight;

    // Reactions system
    private Reactions reactions;

    // Community features
    private Boolean isGuestPost;
    private String guestAuthorEmail;
    private String guestAuthorStatus; // draft, pending, approved, rejected

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Author {
        private Integer id;
        private String name;
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reactions {
        private Integer likes;
        private Integer upvotes;
        private Map<String, Integer> emojis; // emoji -> count (👍, ❤️, 🔥, etc.)
        private List<Integer> userLikes; // User IDs who liked
        private List<Integer> userUpvotes; // User IDs who upvoted

        public Reactions() {
            this.likes = 0;
            this.upvotes = 0;
            this.emojis = new HashMap<>();
            this.userLikes = new java.util.ArrayList<>();
            this.userUpvotes = new java.util.ArrayList<>();
        }
    }
}
