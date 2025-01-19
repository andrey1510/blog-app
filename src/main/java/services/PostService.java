package services;

import dto.PostDto;
import models.Post;

import java.util.UUID;

public interface PostService {
    Post getPostById(UUID id);

    Post createPost(PostDto postDto);

    Post updatePost(UUID id, PostDto postDto);

    void deletePost(UUID id);
}
