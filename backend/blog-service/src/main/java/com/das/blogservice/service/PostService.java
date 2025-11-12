package com.das.blogservice.service;

import com.das.blogservice.model.Post;
import com.das.blogservice.model.PostInput;
import com.das.blogservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByPublishedAtDesc();
    }

    public List<Post> getPublishedPosts() {
        return postRepository.findByPublishedOrderByPublishedAtDesc(true);
    }

    public Post getPostById(String id) {
        return postRepository.findById(id).orElse(null);
    }

    public Post getPostBySlug(String slug) {
        return postRepository.findBySlug(slug).orElse(null);
    }

    public Post createPost(PostInput input, Post.Author author) {
        Post post = new Post();
        post.setTitle(input.getTitle());
        post.setSlug(generateSlug(input.getSlug(), input.getTitle()));
        post.setExcerpt(input.getExcerpt());
        post.setContent(input.getContent());
        post.setTags(input.getTags());
        post.setCategory(input.getCategory());
        post.setPublished(input.getPublished() != null ? input.getPublished() : false);
        post.setAuthor(author);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setViews(0);

        if (post.getPublished()) {
            post.setPublishedAt(LocalDateTime.now());
        }

        return postRepository.save(post);
    }

    public Post updatePost(String id, PostInput input) {
        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            return null;
        }

        post.setTitle(input.getTitle());
        post.setSlug(generateSlug(input.getSlug(), input.getTitle()));
        post.setExcerpt(input.getExcerpt());
        post.setContent(input.getContent());
        post.setTags(input.getTags());
        post.setCategory(input.getCategory());

        Boolean wasPublished = post.getPublished();
        post.setPublished(input.getPublished() != null ? input.getPublished() : false);

        // Set publishedAt if publishing for the first time
        if (!wasPublished && post.getPublished()) {
            post.setPublishedAt(LocalDateTime.now());
        }

        post.setUpdatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    public boolean deletePost(String id) {
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Post> searchPosts(String keyword) {
        return postRepository.searchByTitleOrContent(keyword);
    }

    public List<Post> getPostsByCategory(String category) {
        return postRepository.findByCategory(category);
    }

    public List<Post> getPostsByTag(String tag) {
        return postRepository.findByTagsContaining(tag);
    }

    public List<String> getAllCategories() {
        return postRepository.findAll().stream()
                .map(Post::getCategory)
                .filter(category -> category != null && !category.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> getAllTags() {
        return postRepository.findAll().stream()
                .flatMap(post -> post.getTags() != null ? post.getTags().stream() : null)
                .filter(tag -> tag != null && !tag.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public Post incrementViews(String id) {
        Post post = postRepository.findById(id).orElse(null);
        if (post != null) {
            post.setViews(post.getViews() != null ? post.getViews() + 1 : 1);
            return postRepository.save(post);
        }
        return null;
    }

    private String generateSlug(String providedSlug, String title) {
        if (providedSlug != null && !providedSlug.isEmpty()) {
            return providedSlug;
        }
        return title.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }
}
