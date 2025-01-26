package com.blog.repositories;

import com.blog.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    Page<Post> findByTagsName(String tagName, Pageable pageable);

    Page<Post> findAll( Pageable pageable);

}
