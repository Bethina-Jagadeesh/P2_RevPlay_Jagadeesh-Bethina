package com.rev.app.service;

import com.rev.app.dto.PodcastRequestDto;
import com.rev.app.dto.PodcastResponseDto;
import com.rev.app.entity.Podcast;
import com.rev.app.mapper.IPodcastMapper;
import com.rev.app.repository.IPodcastRepository;
import org.springframework.stereotype.Service;
import com.rev.app.service.IPodcastService;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PodcastServiceImpl implements IPodcastService {

    private final IPodcastRepository podcastRepository;

    public PodcastServiceImpl(IPodcastRepository podcastRepository) {
        this.podcastRepository = podcastRepository;
    }

    @Override
    public PodcastResponseDto createPodcast(PodcastRequestDto requestDto) {
        Podcast podcast = IPodcastMapper.toEntity(requestDto);
        podcast.setCreatedAt(java.time.LocalDateTime.now());
        podcast = podcastRepository.save(podcast);
        return IPodcastMapper.toResponseDto(podcast);
    }

    @Override
    public PodcastResponseDto getPodcastById(int id) {
        return podcastRepository.findById(id)
                .map(IPodcastMapper::toResponseDto)
                .orElse(null);
    }

    @Override
    public List<PodcastResponseDto> getAllPodcasts() {
        return podcastRepository.findAll().stream()
                .map(IPodcastMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PodcastResponseDto> getPodcastsByArtistId(int artistId) {
        return podcastRepository.findByArtistId(artistId).stream()
                .map(IPodcastMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public PodcastResponseDto updatePodcast(int id, PodcastRequestDto requestDto) {
        Podcast existing = podcastRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setTitle(requestDto.getTitle());
            existing.setDescription(requestDto.getDescription());
            podcastRepository.save(existing);
            return IPodcastMapper.toResponseDto(existing);
        }
        return null;
    }

    @Override
    public void deletePodcast(int id) {
        podcastRepository.deleteById(id);
    }
}
