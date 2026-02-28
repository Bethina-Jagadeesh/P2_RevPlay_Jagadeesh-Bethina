package com.rev.app.service;

import com.rev.app.dto.SongRequestDto;
import com.rev.app.dto.SongResponseDto;
import java.util.List;

public interface ISongService {
    SongResponseDto createSong(SongRequestDto requestDto);

    SongResponseDto getSongById(int id);

    List<SongResponseDto> getAllSongs();

    List<SongResponseDto> getSongsByArtistId(int artistId);

    List<SongResponseDto> getSongsByGenreId(int genreId);

    List<SongResponseDto> getSongsByAlbumId(int albumId);

    List<SongResponseDto> searchSongs(String keyword);

    List<SongResponseDto> getTopSongs();

    SongResponseDto updateSong(int id, SongRequestDto requestDto);

    void deleteSong(int id);

    void incrementPlayCount(int songId);
}
