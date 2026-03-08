package com.rev.app.service;

import com.rev.app.dto.PodcastEpisodeRequestDto;
import com.rev.app.dto.PodcastEpisodeResponseDto;
import java.util.List;

public interface IPodcastEpisodeService {
    PodcastEpisodeResponseDto createEpisode(PodcastEpisodeRequestDto requestDto);

    PodcastEpisodeResponseDto getEpisodeById(int id);

    List<PodcastEpisodeResponseDto> getEpisodesByPodcastId(int podcastId);

    PodcastEpisodeResponseDto updateEpisode(int id, PodcastEpisodeRequestDto requestDto);

    void deleteEpisode(int id);
}
