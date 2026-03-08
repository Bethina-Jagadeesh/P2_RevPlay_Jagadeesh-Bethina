package com.rev.app.service;

import com.rev.app.dto.PodcastEpisodeRequestDto;
import com.rev.app.dto.PodcastEpisodeResponseDto;
import com.rev.app.entity.PodcastEpisode;
import com.rev.app.mapper.PodcastEpisodeMapper;
import com.rev.app.repository.IPodcastEpisodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PodcastEpisodeServiceImplTest {

    @Mock
    private IPodcastEpisodeRepository episodeRepository;

    @Mock
    private PodcastEpisodeMapper podcastEpisodeMapper;

    @InjectMocks
    private PodcastEpisodeServiceImpl episodeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateEpisode() {
        PodcastEpisodeRequestDto request = new PodcastEpisodeRequestDto();
        when(podcastEpisodeMapper.toEntity(any())).thenReturn(new PodcastEpisode());
        when(episodeRepository.save(any())).thenReturn(new PodcastEpisode());
        when(podcastEpisodeMapper.toResponseDto(any())).thenReturn(new PodcastEpisodeResponseDto());

        episodeService.createEpisode(request);

        verify(episodeRepository).save(any());
    }

    @Test
    void testGetEpisodeById() {
        when(episodeRepository.findById(1)).thenReturn(Optional.of(new PodcastEpisode()));
        when(podcastEpisodeMapper.toResponseDto(any())).thenReturn(new PodcastEpisodeResponseDto());

        episodeService.getEpisodeById(1);

        verify(episodeRepository).findById(1);
    }
}
