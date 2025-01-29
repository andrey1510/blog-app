package com.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import com.blog.models.Tag;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PostDto {

    private String title;

    private String text;

    private MultipartFile image;

    private String tags;

}