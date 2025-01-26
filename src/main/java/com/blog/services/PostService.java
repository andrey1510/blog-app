package com.blog.services;

import com.blog.dto.PostDto;
import com.blog.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostService {

    Page<Post> getPostsByTag(String tag, int page, int size);

    Page<Post> getAllPosts(int page, int size);

    Post getPostById(Integer id);

    Post createPost(PostDto postDto);

    Post updatePost(Integer id, PostDto postDto);

    void deletePost(Integer id);

}
