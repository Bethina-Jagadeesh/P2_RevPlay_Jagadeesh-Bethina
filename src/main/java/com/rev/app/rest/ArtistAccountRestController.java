package com.rev.app.rest;

import com.rev.app.dto.ArtistAccountRequestDto;
import com.rev.app.dto.ArtistAccountResponseDto;
import com.rev.app.service.IArtistAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
public class ArtistAccountRestController {

    private final IArtistAccountService artistService;

    public ArtistAccountRestController(IArtistAccountService artistService) {
        this.artistService = artistService;
    }

    @PostMapping
    public ResponseEntity<ArtistAccountResponseDto> createArtist(@RequestBody ArtistAccountRequestDto requestDto) {
        return new ResponseEntity<>(artistService.createArtist(requestDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistAccountResponseDto> getArtistById(@PathVariable int id) {
        ArtistAccountResponseDto artist = artistService.getArtistById(id);
        return artist != null ? ResponseEntity.ok(artist) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<ArtistAccountResponseDto>> getAllArtists() {
        return ResponseEntity.ok(artistService.getAllArtists());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArtistAccountResponseDto> updateArtist(@PathVariable int id,
            @RequestBody ArtistAccountRequestDto requestDto) {
        ArtistAccountResponseDto updated = artistService.updateArtist(id, requestDto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable int id) {
        artistService.deleteArtist(id);
        return ResponseEntity.noContent().build();
    }
}
