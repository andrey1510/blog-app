package com.blog.services;

import com.blog.dto.PostDto;
import com.blog.models.Post;
import org.springframework.data.domain.Page;

public interface PostService {

    Page<Post> getPostsByTag(String tag, int page, int size);

    Page<Post> getAllPosts(int page, int size);

    Post getPostById(Integer id);

    Post createPost(PostDto postDto);

    Post updatePost(Integer id, PostDto postDto);

    void deletePost(Integer id);

    String getPreviewText(Post post);

    int getCommentCount(Post post);
}
