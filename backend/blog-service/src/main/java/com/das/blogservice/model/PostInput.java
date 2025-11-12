package com.das.blogservice.model;

import lombok.Data;

import java.util.List;

@Data
public class PostInput {
    private String title;
    private String slug;
    private String excerpt;
    private String content;
    private List<String> tags;
    private String category;
    private Boolean published;
}
