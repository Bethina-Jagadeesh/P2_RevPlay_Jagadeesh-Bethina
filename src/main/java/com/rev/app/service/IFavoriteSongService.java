package com.rev.app.service;

import com.rev.app.dto.FavoriteSongRequestDto;
import com.rev.app.dto.FavoriteSongResponseDto;

import java.util.List;

public interface IFavoriteSongService {
    FavoriteSongResponseDto addFavorite(FavoriteSongRequestDto requestDto);

    List<FavoriteSongResponseDto> getFavoritesByUser(int userId);

    void removeFavorite(int userId, int songId);

    boolean isFavorite(int userId, int songId);

    boolean toggleFavorite(int userId, int songId);

    long getFavoriteCountForSongs(List<Integer> songIds);

    List<com.rev.app.dto.UserAccountResponseDto> getUsersWhoFavoritedSongs(List<Integer> songIds);

}
