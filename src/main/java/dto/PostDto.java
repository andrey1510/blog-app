package dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import models.Tag;

import java.util.Set;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PostDto {

    private String title;

    private String text;

    private Set<Tag> tags;
}
