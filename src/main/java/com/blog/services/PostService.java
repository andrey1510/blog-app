package com.blog.services;

import com.blog.dto.PostDto;
import com.blog.models.Post;

import java.util.UUID;

public interface PostService {
    Post getPostById(UUID id);

    Post createPost(PostDto postDto);

    Post updatePost(UUID id, PostDto postDto);

    void deletePost(UUID id);
}
