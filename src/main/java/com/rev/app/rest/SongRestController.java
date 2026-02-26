package com.rev.app.rest;

import com.rev.app.dto.SongRequestDto;
import com.rev.app.dto.SongResponseDto;
import com.rev.app.service.ISongService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/songs")
public class SongRestController {

    private final ISongService songService;

    public SongRestController(ISongService songService) {
        this.songService = songService;
    }

    @PostMapping
    public ResponseEntity<SongResponseDto> createSong(@RequestBody SongRequestDto requestDto) {
        return new ResponseEntity<>(songService.createSong(requestDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongResponseDto> getSongById(@PathVariable int id) {
        SongResponseDto song = songService.getSongById(id);
        return song != null ? ResponseEntity.ok(song) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<SongResponseDto>> getAllSongs() {
        return ResponseEntity.ok(songService.getAllSongs());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SongResponseDto> updateSong(@PathVariable int id, @RequestBody SongRequestDto requestDto) {
        SongResponseDto updated = songService.updateSong(id, requestDto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable int id) {
        songService.deleteSong(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/play")
    public ResponseEntity<Void> playSong(@PathVariable int id) {
        songService.incrementPlayCount(id);
        return ResponseEntity.ok().build();
    }
}
