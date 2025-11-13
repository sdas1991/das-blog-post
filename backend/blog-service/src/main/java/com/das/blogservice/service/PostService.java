package com.das.blogservice.service;

import com.das.blogservice.model.Post;
import com.das.blogservice.model.PostInput;
import com.das.blogservice.model.EmojiCount;
import com.das.blogservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    @Autowired(required = false)
    private JavaMailSender mailSender;

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
        post.setModule(input.getModule());
        post.setWeight(input.getWeight() != null ? input.getWeight() : 1);
        post.setPublished(input.getPublished() != null ? input.getPublished() : false);
        post.setAuthor(author);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setViews(0);
        post.setReactions(new Post.Reactions());

        // Handle guest posts
        if (input.getIsGuestPost() != null && input.getIsGuestPost()) {
            post.setIsGuestPost(true);
            post.setGuestAuthorEmail(input.getGuestAuthorEmail());
            post.setGuestAuthorStatus("pending");
            post.setPublished(false); // Guest posts start as unpublished
        } else {
            post.setIsGuestPost(false);
        }

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
        post.setModule(input.getModule());

        if (input.getWeight() != null) {
            post.setWeight(input.getWeight());
        }

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

    public List<String> getAllModules() {
        return postRepository.findAll().stream()
                .map(Post::getModule)
                .filter(module -> module != null && !module.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Post> getPostsByModule(String module) {
        return postRepository.findByModule(module);
    }

    public List<Post> getPublishedPostsByModule(String module) {
        return postRepository.findByModuleAndPublished(module, true);
    }

    // Trending posts algorithm: combines weight, reactions, views, and recency
    public List<Post> getTrendingPosts(Integer limit) {
        List<Post> publishedPosts = postRepository.findByPublished(true);

        // Calculate trending score for each post
        List<Post> postsWithScores = publishedPosts.stream()
                .peek(post -> {
                    double score = calculateTrendingScore(post);
                    // Store score in a transient field or return as part of result
                })
                .sorted((p1, p2) -> Double.compare(
                        calculateTrendingScore(p2),
                        calculateTrendingScore(p1)
                ))
                .limit(limit != null ? limit : 10)
                .collect(Collectors.toList());

        return postsWithScores;
    }

    private double calculateTrendingScore(Post post) {
        // Base weight (1-10, admin defined)
        double baseWeight = post.getWeight() != null ? post.getWeight() : 1;

        // Reaction score (likes + upvotes * 2 + emojis)
        int reactionScore = 0;
        if (post.getReactions() != null) {
            reactionScore += post.getReactions().getLikes() != null ? post.getReactions().getLikes() : 0;
            reactionScore += (post.getReactions().getUpvotes() != null ? post.getReactions().getUpvotes() : 0) * 2;
            if (post.getReactions().getEmojis() != null) {
                reactionScore += post.getReactions().getEmojis().values().stream()
                        .mapToInt(Integer::intValue)
                        .sum();
            }
        }

        // View score
        int views = post.getViews() != null ? post.getViews() : 0;

        // Recency factor (decay over time)
        double recencyFactor = 1.0;
        if (post.getPublishedAt() != null) {
            long daysOld = ChronoUnit.DAYS.between(post.getPublishedAt(), LocalDateTime.now());
            recencyFactor = 1.0 / (1.0 + daysOld / 7.0); // Decay weekly
        }

        // Final score formula
        return (baseWeight * 100) + (reactionScore * 10) + (views * 0.5) * recencyFactor;
    }

    // Reactions methods
    public Post reactToPost(String postId, String reactionType, String emoji, Integer userId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return null;
        }

        if (post.getReactions() == null) {
            post.setReactions(new Post.Reactions());
        }

        Post.Reactions reactions = post.getReactions();

        switch (reactionType.toLowerCase()) {
            case "like":
                if (!reactions.getUserLikes().contains(userId)) {
                    reactions.setLikes(reactions.getLikes() + 1);
                    reactions.getUserLikes().add(userId);
                } else {
                    reactions.setLikes(Math.max(0, reactions.getLikes() - 1));
                    reactions.getUserLikes().remove(userId);
                }
                break;

            case "upvote":
                if (!reactions.getUserUpvotes().contains(userId)) {
                    reactions.setUpvotes(reactions.getUpvotes() + 1);
                    reactions.getUserUpvotes().add(userId);
                } else {
                    reactions.setUpvotes(Math.max(0, reactions.getUpvotes() - 1));
                    reactions.getUserUpvotes().remove(userId);
                }
                break;

            case "emoji":
                if (emoji != null && !emoji.isEmpty()) {
                    reactions.getEmojis().put(emoji,
                            reactions.getEmojis().getOrDefault(emoji, 0) + 1);
                }
                break;
        }

        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    // Guest post methods
    public List<Post> getGuestPosts(String status) {
        if (status != null && !status.isEmpty()) {
            return postRepository.findByGuestAuthorStatus(status);
        }
        return postRepository.findByIsGuestPost(true);
    }

    public Post approveGuestPost(String postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null || !post.getIsGuestPost()) {
            return null;
        }

        post.setGuestAuthorStatus("approved");
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        Post savedPost = postRepository.save(post);

        // Send email notification to guest author
        sendGuestPostApprovalEmail(post);

        return savedPost;
    }

    public Post rejectGuestPost(String postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null || !post.getIsGuestPost()) {
            return null;
        }

        post.setGuestAuthorStatus("rejected");
        post.setUpdatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    private void sendGuestPostApprovalEmail(Post post) {
        if (mailSender == null || post.getGuestAuthorEmail() == null) {
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(post.getGuestAuthorEmail());
            message.setSubject("Your Guest Post Has Been Published!");
            message.setText(
                    "Hi,\n\n" +
                            "Great news! Your guest post titled \"" + post.getTitle() + "\" " +
                            "has been reviewed and published on our blog.\n\n" +
                            "You can view it at: [Blog URL]/blog/" + post.getSlug() + "\n\n" +
                            "Thank you for your contribution!\n\n" +
                            "Best regards,\n" +
                            "The Blog Team"
            );
            mailSender.send(message);
        } catch (Exception e) {
            // Log error but don't fail the operation
            System.err.println("Failed to send email: " + e.getMessage());
        }
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
