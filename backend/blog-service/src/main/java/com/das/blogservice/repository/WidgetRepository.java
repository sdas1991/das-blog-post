package com.das.blogservice.repository;

import com.das.blogservice.model.Widget;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WidgetRepository extends MongoRepository<Widget, String> {

    List<Widget> findByActiveOrderByDisplayOrderAsc(Boolean active);

    List<Widget> findByPostId(String postId);

    List<Widget> findAllByOrderByDisplayOrderAsc();
}
