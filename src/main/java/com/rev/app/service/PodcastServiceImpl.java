package com.rev.app.service;

import com.rev.app.dto.PodcastRequestDto;
import com.rev.app.dto.PodcastResponseDto;
import com.rev.app.entity.Podcast;
import com.rev.app.mapper.PodcastMapper;
import com.rev.app.repository.IPodcastRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PodcastServiceImpl implements IPodcastService {

    private final IPodcastRepository podcastRepository;
    private final PodcastMapper podcastMapper;

    public PodcastServiceImpl(IPodcastRepository podcastRepository, PodcastMapper podcastMapper) {
        this.podcastRepository = podcastRepository;
        this.podcastMapper = podcastMapper;
    }

    @Override
    public PodcastResponseDto createPodcast(PodcastRequestDto requestDto) {
        Podcast podcast = podcastMapper.toEntity(requestDto);
        podcast.setCreatedAt(LocalDateTime.now());
        podcast = podcastRepository.save(podcast);
        return podcastMapper.toResponseDto(podcast);
    }

    @Override
    public PodcastResponseDto getPodcastById(int id) {
        return podcastRepository.findById(id)
                .map(podcastMapper::toResponseDto)
                .orElse(null);
    }

    @Override
    public List<PodcastResponseDto> getAllPodcasts() {
        return podcastRepository.findAll().stream()
                .map(podcastMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PodcastResponseDto> getPodcastsByArtistId(int artistId) {
        return podcastRepository.findByArtistId(artistId).stream()
                .map(podcastMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public PodcastResponseDto updatePodcast(int id, PodcastRequestDto requestDto) {
        Podcast existing = podcastRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setTitle(requestDto.getTitle());
            existing.setDescription(requestDto.getDescription());
            podcastRepository.save(existing);
            return podcastMapper.toResponseDto(existing);
        }
        return null;
    }

    @Override
    public void deletePodcast(int id) {
        podcastRepository.deleteById(id);
    }
}
