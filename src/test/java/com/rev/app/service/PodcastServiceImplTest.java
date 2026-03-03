package com.rev.app.service;

import com.rev.app.dto.PodcastRequestDto;
import com.rev.app.dto.PodcastResponseDto;
import com.rev.app.entity.Podcast;
import com.rev.app.mapper.PodcastMapper;
import com.rev.app.repository.IPodcastRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PodcastServiceImplTest {

    @Mock
    private IPodcastRepository podcastRepository;

    @Mock
    private PodcastMapper podcastMapper;

    @InjectMocks
    private PodcastServiceImpl podcastService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePodcast() {
        PodcastRequestDto request = new PodcastRequestDto();
        when(podcastMapper.toEntity(any())).thenReturn(new Podcast());
        when(podcastRepository.save(any())).thenReturn(new Podcast());
        when(podcastMapper.toResponseDto(any())).thenReturn(new PodcastResponseDto());

        podcastService.createPodcast(request);

        verify(podcastRepository).save(any());
    }

    @Test
    void testGetPodcastById() {
        when(podcastRepository.findById(1)).thenReturn(Optional.of(new Podcast()));
        when(podcastMapper.toResponseDto(any())).thenReturn(new PodcastResponseDto());

        podcastService.getPodcastById(1);

        verify(podcastRepository).findById(1);
    }
}
