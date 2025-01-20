package services;

import dto.PostDto;
import exceptions.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import models.Post;
import org.springframework.stereotype.Service;
import repositories.PostRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

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