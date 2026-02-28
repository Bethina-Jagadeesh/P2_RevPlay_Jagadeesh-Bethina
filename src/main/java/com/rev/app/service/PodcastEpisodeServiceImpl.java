package com.rev.app.service;

import com.rev.app.dto.PodcastEpisodeRequestDto;
import com.rev.app.dto.PodcastEpisodeResponseDto;
import com.rev.app.entity.PodcastEpisode;
import com.rev.app.mapper.PodcastEpisodeMapper;
import com.rev.app.repository.IPodcastEpisodeRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PodcastEpisodeServiceImpl implements IPodcastEpisodeService {

    private final IPodcastEpisodeRepository episodeRepository;
    private final PodcastEpisodeMapper podcastEpisodeMapper;

    public PodcastEpisodeServiceImpl(IPodcastEpisodeRepository episodeRepository,
                                     PodcastEpisodeMapper podcastEpisodeMapper) {
        this.episodeRepository = episodeRepository;
        this.podcastEpisodeMapper = podcastEpisodeMapper;
    }

    @Override
    public PodcastEpisodeResponseDto createEpisode(PodcastEpisodeRequestDto requestDto) {
        PodcastEpisode episode = podcastEpisodeMapper.toEntity(requestDto);
        episode.setCreatedAt(LocalDateTime.now());
        episode = episodeRepository.save(episode);
        return podcastEpisodeMapper.toResponseDto(episode);
    }

    @Override
    public PodcastEpisodeResponseDto getEpisodeById(int id) {
        return episodeRepository.findById(id)
                .map(podcastEpisodeMapper::toResponseDto)
                .orElse(null);
    }

    @Override
    public List<PodcastEpisodeResponseDto> getEpisodesByPodcastId(int podcastId) {
        return episodeRepository.findByPodcastId(podcastId).stream()
                .map(podcastEpisodeMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public PodcastEpisodeResponseDto updateEpisode(int id, PodcastEpisodeRequestDto requestDto) {
        PodcastEpisode existing = episodeRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setTitle(requestDto.getTitle());
            episodeRepository.save(existing);
            return podcastEpisodeMapper.toResponseDto(existing);
        }
        return null;
    }

    @Override
    public void deleteEpisode(int id) {
        episodeRepository.deleteById(id);
    }
}
