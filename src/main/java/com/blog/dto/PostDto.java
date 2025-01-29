package com.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

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