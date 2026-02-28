package com.rev.app.service;

import com.rev.app.dto.PlaylistRequestDto;
import com.rev.app.dto.PlaylistResponseDto;
import com.rev.app.entity.Playlist;
import com.rev.app.entity.PlaylistSong;
import com.rev.app.mapper.IPlaylistMapper;
import com.rev.app.repository.IPlaylistRepository;
import com.rev.app.repository.IPlaylistSongRepository;
import org.springframework.stereotype.Service;
import com.rev.app.service.IPlaylistService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaylistServiceImpl implements IPlaylistService {

    private final IPlaylistRepository playlistRepository;
    private final IPlaylistSongRepository playlistSongRepository;

    public PlaylistServiceImpl(IPlaylistRepository playlistRepository,
            IPlaylistSongRepository playlistSongRepository) {
        this.playlistRepository = playlistRepository;
        this.playlistSongRepository = playlistSongRepository;
    }

    @Override
    public PlaylistResponseDto createPlaylist(PlaylistRequestDto requestDto) {
        Playlist playlist = IPlaylistMapper.toEntity(requestDto);
        playlist.setCreatedAt(LocalDateTime.now());
        playlist.setUpdatedAt(LocalDateTime.now());
        playlist = playlistRepository.save(playlist);
        return IPlaylistMapper.toResponseDto(playlist);
    }

    @Override
    public PlaylistResponseDto getPlaylistById(int id) {
        return playlistRepository.findById(id)
                .map(IPlaylistMapper::toResponseDto)
                .orElse(null);
    }

    @Override
    public List<PlaylistResponseDto> getAllPlaylists() {
        return playlistRepository.findAll().stream()
                .map(IPlaylistMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlaylistResponseDto> getPlaylistsByUserId(int userId) {
        return playlistRepository.findByUserId(userId).stream()
                .map(IPlaylistMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlaylistResponseDto> getPublicPlaylists() {
        return playlistRepository.findByPrivacyStatus("public").stream()
                .map(IPlaylistMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public PlaylistResponseDto updatePlaylist(int id, PlaylistRequestDto requestDto) {
        Playlist existingPlaylist = playlistRepository.findById(id).orElse(null);
        if (existingPlaylist != null) {
            existingPlaylist.setName(requestDto.getName());
            existingPlaylist.setDescription(requestDto.getDescription());
            existingPlaylist.setPrivacyStatus(requestDto.getPrivacyStatus());
            existingPlaylist.setUpdatedAt(LocalDateTime.now());
            playlistRepository.save(existingPlaylist);
            return IPlaylistMapper.toResponseDto(existingPlaylist);
        }
        return null;
    }

    @Override
    public void deletePlaylist(int id) {
        playlistRepository.deleteById(id);
    }

    @Override
    public void addSongToPlaylist(int playlistId, int songId) {
        PlaylistSong ps = new PlaylistSong(playlistId, songId, LocalDateTime.now());
        playlistSongRepository.save(ps);
    }

    @Override
    public void removeSongFromPlaylist(int playlistId, int songId) {
        playlistSongRepository.deleteByPlaylistIdAndSongId(playlistId, songId);
    }

    @Override
    public List<Integer> getSongIdsInPlaylist(int playlistId) {
        return playlistSongRepository.findByPlaylistId(playlistId).stream()
                .map(PlaylistSong::getSongId)
                .collect(Collectors.toList());
    }
}
