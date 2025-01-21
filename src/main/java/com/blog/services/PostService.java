package com.blog.services;

import com.blog.dto.PostDto;
import com.blog.models.Post;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface PostService {
    Page<Post> getPosts(String tag, int page, int size);

    Post getPostById(UUID id);

    Post createPost(PostDto postDto);

    Post updatePost(UUID id, PostDto postDto);

    void deletePost(UUID id);
}
