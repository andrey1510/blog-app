package com.blog.services;

import com.blog.dto.PostDto;
import com.blog.exceptions.MaxImageSizeExceededException;
import com.blog.exceptions.WrongImageTypeException;
import com.blog.models.Tag;
import com.blog.repositories.TagRepository;
import com.blog.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import com.blog.models.Post;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.blog.repositories.PostRepository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    @Value("${images.upload-directory}")
    private String uploadDirectory;

    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    @Override
    public Page<Post> getPostsByTag(String tag, int page, int size) {
        List<Post> posts = postRepository.findByTagsName(tag, page, size);
        long totalPosts = postRepository.countPostsByTag(tag);
        return new PageImpl<>(posts, PageRequest.of(page, size), totalPosts);
    }

    @Override
    public Page<Post> getAllPosts(int page, int size) {
        List<Post> posts = postRepository.findAll(page, size);
        long totalPosts = postRepository.countAllPosts();
        return new PageImpl<>(posts, PageRequest.of(page, size), totalPosts);
    }

    @Override
    public Post getPostById(Integer id) {
        return postRepository.findById(id);
    }

    @Override
    public Post createPost(PostDto postDto) {
        String uniqueFileName = null;
        MultipartFile image = postDto.getImage();
        if (image != null && !image.isEmpty()) {
            validateImage(image);
            uniqueFileName = ImageUtils.generateUniqueImageName(image.getOriginalFilename());
            saveImage(image, uniqueFileName);
        }

        Set<Tag> tags = processTags(postDto.getTags());

        Post post = Post.builder()
            .title(postDto.getTitle())
            .text(postDto.getText())
            .tags(tags)
            .imagePath(uniqueFileName)
            .likes(0)
            .build();

        postRepository.save(post);
        postRepository.savePostTags(post.getId(), tags);
        return post;
    }

    @Override
    public Post updatePost(Integer id, PostDto postDto) {
        Post post = getPostById(id);

        String uniqueFileName = post.getImagePath();
        if (postDto.getImage() == null || postDto.getImage().isEmpty()) {
            if (post.getImagePath() != null) {
                ImageUtils.deleteImageIfExists(post.getImagePath());
                uniqueFileName = null;
            }
        } else {
            validateImage(postDto.getImage());
            uniqueFileName = ImageUtils.generateUniqueImageName(postDto.getImage().getOriginalFilename());
            saveImage(postDto.getImage(), uniqueFileName);
            ImageUtils.deleteImageIfExists(post.getImagePath());
        }

        post.setTitle(postDto.getTitle());
        post.setText(postDto.getText());
        post.setTags(processTags(postDto.getTags()));
        post.setImagePath(uniqueFileName);

        postRepository.save(post);

        postRepository.deletePostTags(post.getId());
        postRepository.savePostTags(post.getId(), post.getTags());

        return post;
    }

    @Override
    public void deletePost(Integer id) {
        ImageUtils.deleteImageIfExists(getPostById(id).getImagePath());
        postRepository.deletePostTags(id);
        postRepository.deleteById(id);
    }

    @Override
    public String getPreviewText(Post post) {
        String[] paragraphs = post.getText().split("\n\n");
        StringBuilder previewText = new StringBuilder();
        String[] lines = paragraphs[0].split("\n");
        int lineCount = 0;
        for (String line : lines) {
            if (lineCount >= 3) break;
            previewText.append(line).append("\n");
            lineCount++;
        }
        return previewText.toString().trim();
    }

    @Override
    public int getCommentCount(Post post) {
        return postRepository.getCommentCount(post.getId());
    }

    private void validateImage(MultipartFile file) {
        if (!ImageUtils.isValidImageExtension(file.getOriginalFilename())) {
            throw new WrongImageTypeException("Недопустимый формат изображения, разрешены: jpeg, jpg, png.");
        }
        if (!ImageUtils.isValidImageSize(file.getSize())) {
            throw new MaxImageSizeExceededException("Размер файла не должен превышать 3 МБ.");
        }
    }

    private void saveImage(MultipartFile file, String relativePath) {
        try {
            Path fullPath = Paths.get(System.getProperty("catalina.base") + uploadDirectory, relativePath);
            Files.createDirectories(fullPath.getParent());
            file.transferTo(fullPath);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении файла: " + relativePath, e);
        }
    }

    private Set<Tag> processTags(String tagString) {
        if (tagString == null || tagString.trim().isEmpty()) return new HashSet<>();
        Set<String> tagNames = Stream.of(tagString.split(","))
            .map(String::trim)
            .distinct()
            .filter(name -> !name.isEmpty())
            .collect(Collectors.toSet());

        Set<Tag> tags = new HashSet<>();
        for (String tagName : tagNames) {
            Optional<Tag> tag = tagRepository.findByName(tagName);
            if (tag.isPresent()) {
                tags.add(tag.get());
            } else {
                Tag newTag = Tag.builder().name(tagName).build();
                tagRepository.save(newTag);
                tags.add(newTag);
            }
        }
        return tags;
    }
}