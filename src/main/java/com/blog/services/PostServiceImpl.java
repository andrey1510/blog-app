package com.blog.services;

import com.blog.dto.PostDto;
import com.blog.exceptions.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import com.blog.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.blog.repositories.PostRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    public Page<Post> getPosts(String tag, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return (tag == null || tag.isEmpty())
            ? postRepository.findAll(pageable)
            : postRepository.findByTagsName(tag, pageable);
    }

    @Override
    public Post getPostById(UUID id) {
        return postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("Пост не найден."));
    }

    @Override
    public Post createPost(PostDto postDto) {
        Post post = Post.builder()
            .title(postDto.getTitle())
            .text(postDto.getText())
            .tags(postDto.getTags())
            .likes(0)
            .build();
        return postRepository.save(post);
    }

    @Override
    public Post updatePost(UUID id, PostDto postDto) {
        Post post = getPostById(id);
        post = Post.builder()
            .id(post.getId())
            .title(postDto.getTitle())
            .text(postDto.getText())
            .tags(post.getTags())
            .build();
        return postRepository.save(post);
    }

    @Override
    public void deletePost(UUID id) {
        postRepository.deleteById(id);
    }
}