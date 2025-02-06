package com.blog.services;

import com.blog.models.Tag;
import com.blog.repositories.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    private Tag tag1;

    private Tag tag2;

    @BeforeEach
    public void setUp() {
        tag1 = new Tag();
        tag1.setId(1);
        tag1.setName("tag1");

        tag2 = new Tag();
        tag2.setId(2);
        tag2.setName("tag2");
    }

    @Test
    public void testGetAllTags() {
        List<Tag> tags = Arrays.asList(tag1, tag2);
        when(tagRepository.findAll()).thenReturn(tags);

        List<Tag> result = tagService.getAllTags();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(tag1));
        assertTrue(result.contains(tag2));
        verify(tagRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllTagsNoTags() {
        when(tagRepository.findAll()).thenReturn(Collections.emptyList());

        List<Tag> result = tagService.getAllTags();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(tagRepository, times(1)).findAll();
    }
}