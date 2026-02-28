package com.rev.app.service;

import com.rev.app.dto.PodcastEpisodeRequestDto;
import com.rev.app.dto.PodcastEpisodeResponseDto;
import com.rev.app.entity.PodcastEpisode;
import com.rev.app.mapper.IPodcastEpisodeMapper;
import com.rev.app.repository.IPodcastEpisodeRepository;
import org.springframework.stereotype.Service;
import com.rev.app.service.IPodcastEpisodeService;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PodcastEpisodeServiceImpl implements IPodcastEpisodeService {

    private final IPodcastEpisodeRepository episodeRepository;

    public PodcastEpisodeServiceImpl(IPodcastEpisodeRepository episodeRepository) {
        this.episodeRepository = episodeRepository;
    }

    @Override
    public PodcastEpisodeResponseDto createEpisode(PodcastEpisodeRequestDto requestDto) {
        PodcastEpisode episode = IPodcastEpisodeMapper.toEntity(requestDto);
        episode.setCreatedAt(java.time.LocalDateTime.now());
        episode = episodeRepository.save(episode);
        return IPodcastEpisodeMapper.toResponseDto(episode);
    }

    @Override
    public PodcastEpisodeResponseDto getEpisodeById(int id) {
        return episodeRepository.findById(id)
                .map(IPodcastEpisodeMapper::toResponseDto)
                .orElse(null);
    }

    @Override
    public List<PodcastEpisodeResponseDto> getEpisodesByPodcastId(int podcastId) {
        return episodeRepository.findByPodcastId(podcastId).stream()
                .map(IPodcastEpisodeMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public PodcastEpisodeResponseDto updateEpisode(int id, PodcastEpisodeRequestDto requestDto) {
        PodcastEpisode existing = episodeRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setTitle(requestDto.getTitle());
            episodeRepository.save(existing);
            return IPodcastEpisodeMapper.toResponseDto(existing);
        }
        return null;
    }

    @Override
    public void deleteEpisode(int id) {
        episodeRepository.deleteById(id);
    }
}
