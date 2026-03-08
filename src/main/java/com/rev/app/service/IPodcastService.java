package com.rev.app.service;

import com.rev.app.dto.PodcastRequestDto;
import com.rev.app.dto.PodcastResponseDto;
import java.util.List;

public interface IPodcastService {
    PodcastResponseDto createPodcast(PodcastRequestDto requestDto);

    PodcastResponseDto getPodcastById(int id);

    List<PodcastResponseDto> getAllPodcasts();

    List<PodcastResponseDto> getPodcastsByArtistId(int artistId);

    PodcastResponseDto updatePodcast(int id, PodcastRequestDto requestDto);

    void deletePodcast(int id);
}
