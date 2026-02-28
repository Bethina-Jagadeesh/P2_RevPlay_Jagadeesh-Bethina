package com.rev.app.service;

import com.rev.app.dto.FavoriteSongRequestDto;
import com.rev.app.dto.FavoriteSongResponseDto;
import com.rev.app.entity.FavoriteSong;
import com.rev.app.mapper.FavoriteSongMapper;
import com.rev.app.repository.IFavoriteSongRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteSongServiceImpl implements IFavoriteSongService {

    private final IFavoriteSongRepository favoriteSongRepository;
    private final FavoriteSongMapper favoriteSongMapper;

    public FavoriteSongServiceImpl(IFavoriteSongRepository favoriteSongRepository,
                                   FavoriteSongMapper favoriteSongMapper) {
        this.favoriteSongRepository = favoriteSongRepository;
        this.favoriteSongMapper = favoriteSongMapper;
    }

    @Override
    public FavoriteSongResponseDto addFavorite(FavoriteSongRequestDto requestDto) {
        if (!favoriteSongRepository.existsByUserIdAndSongId(requestDto.getUserId(), requestDto.getSongId())) {
            FavoriteSong favorite = favoriteSongMapper.toEntity(requestDto);
            favorite = favoriteSongRepository.save(favorite);
            return favoriteSongMapper.toResponseDto(favorite);
        }
        return favoriteSongRepository.findByUserId(requestDto.getUserId()).stream()
                .filter(f -> f.getSongId() == requestDto.getSongId())
                .map(favoriteSongMapper::toResponseDto)
                .findFirst().orElse(null);
    }

    @Override
    public List<FavoriteSongResponseDto> getFavoritesByUser(int userId) {
        return favoriteSongRepository.findByUserId(userId).stream()
                .map(favoriteSongMapper::toResponseDto)
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

    @Override
    public boolean toggleFavorite(int userId, int songId) {
        if (favoriteSongRepository.existsByUserIdAndSongId(userId, songId)) {
            favoriteSongRepository.deleteByUserIdAndSongId(userId, songId);
            return false;
        } else {
            FavoriteSong favorite = FavoriteSong.builder()
                    .userId(userId)
                    .songId(songId)
                    .favoritedAt(java.time.LocalDateTime.now())
                    .build();
            favoriteSongRepository.save(favorite);
            return true;
        }
    }
}
