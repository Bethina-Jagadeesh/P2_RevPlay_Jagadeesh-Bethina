package com.rev.app.service;

import com.rev.app.dto.FavoriteSongRequestDto;
import com.rev.app.dto.FavoriteSongResponseDto;
import com.rev.app.entity.FavoriteSong;
import com.rev.app.mapper.IFavoriteSongMapper;
import com.rev.app.repository.IFavoriteSongRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteSongServiceImpl implements IFavoriteSongService {

    private final IFavoriteSongRepository favoriteSongRepository;

    public FavoriteSongServiceImpl(IFavoriteSongRepository favoriteSongRepository) {
        this.favoriteSongRepository = favoriteSongRepository;
    }

    @Override
    public FavoriteSongResponseDto addFavorite(FavoriteSongRequestDto requestDto) {
        // Only add if not already a favorite
        if (!favoriteSongRepository.existsByUserIdAndSongId(requestDto.getUserId(), requestDto.getSongId())) {
            FavoriteSong favorite = IFavoriteSongMapper.toEntity(requestDto);
            favorite = favoriteSongRepository.save(favorite);
            return IFavoriteSongMapper.toResponseDto(favorite);
        }
        // Already exists - return existing
        return favoriteSongRepository.findByUserId(requestDto.getUserId()).stream()
                .filter(f -> f.getSongId() == requestDto.getSongId())
                .map(IFavoriteSongMapper::toResponseDto)
                .findFirst().orElse(null);
    }

    @Override
    public List<FavoriteSongResponseDto> getFavoritesByUser(int userId) {
        return favoriteSongRepository.findByUserId(userId).stream()
                .map(IFavoriteSongMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void removeFavorite(int userId, int songId) {
        favoriteSongRepository.deleteByUserIdAndSongId(userId, songId);
    }

    @Override
    public boolean isFavorite(int userId, int songId) {
        return favoriteSongRepository.existsByUserIdAndSongId(userId, songId);
    }
}
