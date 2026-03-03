package com.rev.app.service;

import com.rev.app.dto.FavoriteSongRequestDto;
import com.rev.app.dto.FavoriteSongResponseDto;
import com.rev.app.entity.FavoriteSong;
import com.rev.app.mapper.FavoriteSongMapper;
import com.rev.app.repository.IFavoriteSongRepository;
import com.rev.app.repository.IUserAccountRepository;
import com.rev.app.mapper.UserAccountMapper;
import com.rev.app.dto.UserAccountResponseDto;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteSongServiceImpl implements IFavoriteSongService {

    private final IFavoriteSongRepository favoriteSongRepository;
    private final FavoriteSongMapper favoriteSongMapper;
    private final IUserAccountRepository userRepository;
    private final UserAccountMapper userMapper;

    public FavoriteSongServiceImpl(IFavoriteSongRepository favoriteSongRepository,
            FavoriteSongMapper favoriteSongMapper,
            IUserAccountRepository userRepository,
            UserAccountMapper userMapper) {
        this.favoriteSongRepository = favoriteSongRepository;
        this.favoriteSongMapper = favoriteSongMapper;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
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

    @Override
    public long getFavoriteCountForSongs(List<Integer> songIds) {
        if (songIds == null || songIds.isEmpty()) {
            return 0;
        }
        return favoriteSongRepository.countBySongIdIn(songIds);
    }

    @Override
    public List<UserAccountResponseDto> getUsersWhoFavoritedSongs(List<Integer> songIds) {
        if (songIds == null || songIds.isEmpty())
            return List.of();
        List<Integer> userIds = favoriteSongRepository.findBySongIdIn(songIds)
                .stream()
                .map(FavoriteSong::getUserId)
                .distinct()
                .collect(Collectors.toList());
        return userRepository.findByUserIdIn(userIds).stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
