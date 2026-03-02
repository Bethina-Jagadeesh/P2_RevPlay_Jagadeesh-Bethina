package com.rev.app.service;

import com.rev.app.dto.SongRequestDto;
import com.rev.app.dto.SongResponseDto;
import com.rev.app.entity.Song;
import com.rev.app.mapper.SongMapper;
import com.rev.app.repository.IAlbumRepository;
import com.rev.app.repository.IArtistAccountRepository;
import com.rev.app.repository.IGenreRepository;
import com.rev.app.repository.ISongRepository;
import com.rev.app.repository.IFavoriteSongRepository;
import com.rev.app.repository.IListeningHistoryRepository;
import com.rev.app.repository.IPlaylistSongRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SongServiceImpl implements ISongService {

    private final ISongRepository songRepository;
    private final IAlbumRepository albumRepository;
    private final IArtistAccountRepository artistRepository;
    private final IGenreRepository genreRepository;
    private final SongMapper songMapper;
    private final IFavoriteSongRepository favoriteSongRepository;
    private final IListeningHistoryRepository listeningHistoryRepository;
    private final IPlaylistSongRepository playlistSongRepository;

    public SongServiceImpl(ISongRepository songRepository,
            IAlbumRepository albumRepository,
            IArtistAccountRepository artistRepository,
            IGenreRepository genreRepository,
            SongMapper songMapper,
            IFavoriteSongRepository favoriteSongRepository,
            IListeningHistoryRepository listeningHistoryRepository,
            IPlaylistSongRepository playlistSongRepository) {
        this.songRepository = songRepository;
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
        this.genreRepository = genreRepository;
        this.songMapper = songMapper;
        this.favoriteSongRepository = favoriteSongRepository;
        this.listeningHistoryRepository = listeningHistoryRepository;
        this.playlistSongRepository = playlistSongRepository;
    }

    private void populateNames(Song song) {
        if (song == null)
            return;
        artistRepository.findById(song.getArtistId()).ifPresent(artist -> {
            song.setArtistName(artist.getStageName());
            song.setCoverImageUrl(artist.getProfileImageUrl());
        });
        if (song.getAlbumId() != null && song.getAlbumId() > 0) {
            albumRepository.findById(song.getAlbumId()).ifPresent(album -> {
                song.setAlbumName(album.getTitle());
                if (album.getCoverImageUrl() != null && !album.getCoverImageUrl().isEmpty()) {
                    song.setCoverImageUrl(album.getCoverImageUrl());
                }
            });
        }
        if (song.getGenreId() > 0) {
            genreRepository.findById(song.getGenreId()).ifPresent(genre -> song.setGenreName(genre.getGenreName()));
        }
    }

    @Override
    public SongResponseDto createSong(SongRequestDto requestDto) {
        Song song = songMapper.toEntity(requestDto);
        song = songRepository.save(song);
        populateNames(song);
        return songMapper.toResponseDto(song);
    }

    @Override
    public SongResponseDto getSongById(int id) {
        return songRepository.findById(id)
                .map(song -> {
                    populateNames(song);
                    return songMapper.toResponseDto(song);
                })
                .orElse(null);
    }

    @Override
    public List<SongResponseDto> getAllSongs() {
        return songRepository.findAll().stream()
                .peek(this::populateNames)
                .map(songMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SongResponseDto> getSongsByArtistId(int artistId) {
        return songRepository.findByArtistId(artistId).stream()
                .peek(this::populateNames)
                .map(songMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SongResponseDto> getSongsByGenreId(int genreId) {
        return songRepository.findByGenreId(genreId).stream()
                .peek(this::populateNames)
                .map(songMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SongResponseDto> getSongsByAlbumId(int albumId) {
        return songRepository.findByAlbumId(albumId).stream()
                .peek(this::populateNames)
                .map(songMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SongResponseDto> searchSongs(String keyword) {
        return songRepository.findByTitleContainingIgnoreCase(keyword).stream()
                .peek(this::populateNames)
                .map(songMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SongResponseDto> getTopSongs() {
        return songRepository.findAllByOrderByPlayCountDesc().stream()
                .peek(this::populateNames)
                .map(songMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public SongResponseDto updateSong(int id, SongRequestDto requestDto) {
        Song existingSong = songRepository.findById(id).orElse(null);
        if (existingSong != null) {
            existingSong.setTitle(requestDto.getTitle());
            if (requestDto.getGenreId() > 0)
                existingSong.setGenreId(requestDto.getGenreId());
            if (requestDto.getAlbumId() != null)
                existingSong.setAlbumId(requestDto.getAlbumId());
            if (requestDto.getFileUrl() != null)
                existingSong.setFileUrl(requestDto.getFileUrl());
            if (requestDto.getDurationSeconds() > 0)
                existingSong.setDurationSeconds(requestDto.getDurationSeconds());
            songRepository.save(existingSong);
            populateNames(existingSong);
            return songMapper.toResponseDto(existingSong);
        }
        return null;
    }

    @Override
    public void deleteSong(int id) {
        favoriteSongRepository.deleteBySongId(id);
        listeningHistoryRepository.deleteBySongId(id);
        playlistSongRepository.deleteBySongId(id);
        songRepository.deleteById(id);
    }

    @Override
    public void incrementPlayCount(int songId) {
        songRepository.findById(songId).ifPresent(song -> {
            song.setPlayCount(song.getPlayCount() + 1);
            songRepository.save(song);
        });
    }
}
