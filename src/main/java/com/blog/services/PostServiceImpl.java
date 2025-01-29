package com.blog.services;

import com.blog.dto.PostDto;
import com.blog.exceptions.MaxImageSizeExceededException;
import com.blog.exceptions.PostNotFoundException;
import com.blog.exceptions.WrongImageTypeException;
import com.blog.models.Tag;
import com.blog.repositories.TagRepository;
import com.blog.utils.ImageUtils;
import com.blog.utils.TagsUtils;
import lombok.RequiredArgsConstructor;
import com.blog.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.blog.repositories.PostRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    private static final String UPLOAD_DIRECTORY = System.getProperty("catalina.base") + "/uploads/";

    @Override
    @Transactional(readOnly = true)
    public Page<Post> getPostsByTag(String tag, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return postRepository.findByTagsName(tag, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Post> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return postRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Post getPostById(Integer id) {
        return postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("Пост не найден."));
    }

    @Override
    @Transactional
    public Post createPost(PostDto postDto) {
        Set<Tag> tags = TagsUtils.getParsedTags(postDto.getTags()).stream()
            .map(tag -> tagRepository.findByName(tag.getName()).orElseGet(() -> tagRepository.save(tag)))
            .collect(Collectors.toSet());

        String uniqueFileName = null;
        MultipartFile image = postDto.getImage();
        if (image != null && !image.isEmpty()) {
            validateImage(image);
            uniqueFileName = ImageUtils.generateUniqueImageName(image.getOriginalFilename());
            saveImage(image, uniqueFileName);
        }

        Post post = Post.builder()
            .title(postDto.getTitle())
            .text(postDto.getText())
            .tags(tags)
            .imagePath(uniqueFileName)
            .likes(0)
            .build();
        return postRepository.save(post);
    }

    @Override
    @Transactional
    public Post updatePost(Integer id, PostDto postDto) {
        Post post = getPostById(id);

        if (postDto.getImage() == null || postDto.getImage().isEmpty()) {
            if (post.getImagePath() != null) {
                ImageUtils.deleteImageIfExists(post.getImagePath());
            }
        }

        String uniqueFileName = post.getImagePath();
        MultipartFile newImage = postDto.getImage();
        if (newImage != null && !newImage.isEmpty()) {
            validateImage(newImage);
            uniqueFileName = ImageUtils.generateUniqueImageName(newImage.getOriginalFilename());
            saveImage(newImage, uniqueFileName);
            ImageUtils.deleteImageIfExists(post.getImagePath());
        }

        Set<Tag> tags = TagsUtils.getParsedTags(postDto.getTags()).stream()
            .map(tag -> tagRepository.findByName(tag.getName()).orElseGet(() -> tagRepository.save(tag)))
            .collect(Collectors.toSet());

        post.setTitle(postDto.getTitle());
        post.setText(postDto.getText());
        post.setTags(tags);
        post.setImagePath(uniqueFileName);

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public void deletePost(Integer id) {
        ImageUtils.deleteImageIfExists(getPostById(id).getImagePath());
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
        if (post.getComments() == null) return 0;
        return post.getComments().size();
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
            Path fullPath = Paths.get(UPLOAD_DIRECTORY, relativePath);
            Files.createDirectories(fullPath.getParent());
            file.transferTo(fullPath);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении файла: " + relativePath, e);
        }
    }
}