package com.rev.app.rest;

import com.rev.app.dto.PlaylistRequestDto;
import com.rev.app.dto.PlaylistResponseDto;
import com.rev.app.service.IPlaylistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistRestController {

    private final IPlaylistService playlistService;

    public PlaylistRestController(IPlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @PostMapping
    public ResponseEntity<PlaylistResponseDto> createPlaylist(@RequestBody PlaylistRequestDto requestDto) {
        return new ResponseEntity<>(playlistService.createPlaylist(requestDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistResponseDto> getPlaylistById(@PathVariable int id) {
        PlaylistResponseDto playlist = playlistService.getPlaylistById(id);
        return playlist != null ? ResponseEntity.ok(playlist) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<PlaylistResponseDto>> getAllPlaylists() {
        return ResponseEntity.ok(playlistService.getAllPlaylists());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlaylistResponseDto> updatePlaylist(@PathVariable int id,
            @RequestBody PlaylistRequestDto requestDto) {
        PlaylistResponseDto updated = playlistService.updatePlaylist(id, requestDto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable int id) {
        playlistService.deletePlaylist(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/songs/{songId}")
    public ResponseEntity<Void> addSongToPlaylist(@PathVariable int id, @PathVariable int songId) {
        playlistService.addSongToPlaylist(id, songId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/songs/{songId}")
    public ResponseEntity<Void> removeSongFromPlaylist(@PathVariable int id, @PathVariable int songId) {
        playlistService.removeSongFromPlaylist(id, songId);
        return ResponseEntity.noContent().build();
    }
}
