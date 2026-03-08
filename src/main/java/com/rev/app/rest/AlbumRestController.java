package com.rev.app.rest;

import com.rev.app.dto.AlbumRequestDto;
import com.rev.app.dto.AlbumResponseDto;
import com.rev.app.service.IAlbumService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
public class AlbumRestController {

    private final IAlbumService albumService;

    public AlbumRestController(IAlbumService albumService) {
        this.albumService = albumService;
    }

    @PostMapping
    public ResponseEntity<AlbumResponseDto> createAlbum(@RequestBody AlbumRequestDto requestDto) {
        return new ResponseEntity<>(albumService.createAlbum(requestDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> getAlbumById(@PathVariable int id) {
        AlbumResponseDto album = albumService.getAlbumById(id);
        return album != null ? ResponseEntity.ok(album) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<AlbumResponseDto>> getAllAlbums() {
        return ResponseEntity.ok(albumService.getAllAlbums());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> updateAlbum(@PathVariable int id, @RequestBody AlbumRequestDto requestDto) {
        AlbumResponseDto updated = albumService.updateAlbum(id, requestDto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable int id) {
        albumService.deleteAlbum(id);
        return ResponseEntity.noContent().build();
    }
}
