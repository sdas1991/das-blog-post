package com.das.blogservice.controller;

import com.das.blogservice.model.Post;
import com.das.blogservice.model.PostInput;
import com.das.blogservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @QueryMapping
    public List<Post> posts(@Argument Map<String, Object> filters) {
        if (filters == null || filters.isEmpty()) {
            return postService.getAllPosts();
        }

        Boolean published = filters.containsKey("published")
            ? (Boolean) filters.get("published")
            : null;

        if (published != null && published) {
            return postService.getPublishedPosts();
        }

        String category = (String) filters.get("category");
        if (category != null) {
            return postService.getPostsByCategory(category);
        }

        String tag = (String) filters.get("tag");
        if (tag != null) {
            return postService.getPostsByTag(tag);
        }

        String search = (String) filters.get("search");
        if (search != null) {
            return postService.searchPosts(search);
        }

        return postService.getAllPosts();
    }

    @QueryMapping
    public Post post(@Argument String id) {
        Post post = postService.getPostById(id);
        if (post != null) {
            postService.incrementViews(id);
        }
        return post;
    }

    @QueryMapping
    public List<String> categories() {
        return postService.getAllCategories();
    }

    @QueryMapping
    public List<String> tags() {
        return postService.getAllTags();
    }

    @MutationMapping
    public Post createPost(@Argument PostInput input) {
        // In a real application, you would get the author from the JWT token
        Post.Author author = new Post.Author(1, "Admin", "admin@example.com");
        return postService.createPost(input, author);
    }

    @MutationMapping
    public Post updatePost(@Argument String id, @Argument PostInput input) {
        return postService.updatePost(id, input);
    }

    @MutationMapping
    public Boolean deletePost(@Argument String id) {
        return postService.deletePost(id);
    }
}
