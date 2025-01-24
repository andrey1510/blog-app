package com.blog.services;

import com.blog.dto.PostDto;
import com.blog.exceptions.PostNotFoundException;
import com.blog.models.Tag;
import com.blog.repositories.TagRepository;
import lombok.RequiredArgsConstructor;
import com.blog.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.blog.repositories.PostRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Post> getPosts(String tag, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return (tag == null || tag.isEmpty())
            ? postRepository.findAll(pageable)
            : postRepository.findByTagsName(tag, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Post getPostById(Integer id) {

        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("Пост не найден."));
        System.out.println("service get");
        System.out.println(post);
        return post;
    }

    @Override
    @Transactional
    public Post createPost(PostDto postDto) {
        System.out.println("service create");
        System.out.println(postDto);

        Set<Tag> tags = postDto.getParsedTags().stream()
            .map(tag -> tagRepository.findByName(tag.getName()).orElseGet(() -> tagRepository.save(tag)))
            .collect(Collectors.toSet());
        Post post = Post.builder()
            .title(postDto.getTitle())
            .text(postDto.getText())
            .tags(tags)
            .likes(0)
            .build();
        return postRepository.save(post);
    }

    @Override
    @Transactional
    public Post updatePost(Integer id, PostDto postDto) {
        Set<Tag> tags = postDto.getParsedTags().stream()
            .map(tag -> tagRepository.findByName(tag.getName()).orElseGet(() -> tagRepository.save(tag)))
            .collect(Collectors.toSet());
        Post post = getPostById(id);
        post = Post.builder()
            .id(post.getId())
            .title(postDto.getTitle())
            .text(postDto.getText())
            .tags(tags)
            .build();
        return postRepository.save(post);
    }

    @Override
    @Transactional
    public void deletePost(Integer id) {
        postRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getAllPosts(){
        return postRepository.findAll();
    }

}