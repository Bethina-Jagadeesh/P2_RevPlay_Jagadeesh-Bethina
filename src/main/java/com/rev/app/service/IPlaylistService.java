package com.rev.app.service;

import com.rev.app.dto.PlaylistRequestDto;
import com.rev.app.dto.PlaylistResponseDto;
import java.util.List;

public interface IPlaylistService {
    PlaylistResponseDto createPlaylist(PlaylistRequestDto requestDto);

    PlaylistResponseDto getPlaylistById(int id);

    List<PlaylistResponseDto> getAllPlaylists();

    List<PlaylistResponseDto> getPlaylistsByUserId(int userId);

    List<PlaylistResponseDto> getPublicPlaylists();

    PlaylistResponseDto updatePlaylist(int id, PlaylistRequestDto requestDto);

    void deletePlaylist(int id);

    void addSongToPlaylist(int playlistId, int songId);

    void removeSongFromPlaylist(int playlistId, int songId);

    List<Integer> getSongIdsInPlaylist(int playlistId);
}
