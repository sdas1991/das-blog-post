package com.das.blogservice.repository;

import com.das.blogservice.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    Optional<Post> findBySlug(String slug);

    List<Post> findByPublished(Boolean published);

    List<Post> findByCategory(String category);

    List<Post> findByTagsContaining(String tag);

    @Query("{ 'title': { $regex: ?0, $options: 'i' } }")
    List<Post> searchByTitle(String keyword);

    @Query("{ $or: [ { 'title': { $regex: ?0, $options: 'i' } }, { 'content': { $regex: ?0, $options: 'i' } } ] }")
    List<Post> searchByTitleOrContent(String keyword);

    List<Post> findAllByOrderByPublishedAtDesc();

    List<Post> findByPublishedOrderByPublishedAtDesc(Boolean published);
}
