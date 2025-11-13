package com.das.blogservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "widgets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Widget {

    @Id
    private String id;

    private String title;

    private String shortDescription;

    private String imageUrl;  // S3 URL for the widget image

    private String postId;    // Reference to the blog post

    private Integer displayOrder;  // Order in which widgets appear

    private Boolean active;   // Whether widget is currently displayed

    private WidgetConfig config;  // Plugin-specific configuration

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WidgetConfig {
        private String layout;           // Layout type (e.g., "card", "banner", "featured")
        private String backgroundColor;
        private String textColor;
        private String apiEndpoint;      // Custom API endpoint for the widget
        private String linkUrl;          // Where the widget should link to
    }
}
