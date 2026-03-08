package com.rev.app.service;

import com.rev.app.dto.ArtistAccountRequestDto;
import com.rev.app.dto.ArtistAccountResponseDto;
import java.util.List;

public interface IArtistAccountService {
    ArtistAccountResponseDto createArtist(ArtistAccountRequestDto requestDto);

    ArtistAccountResponseDto getArtistById(int id);

    List<ArtistAccountResponseDto> getAllArtists();

    ArtistAccountResponseDto updateArtist(int id, ArtistAccountRequestDto requestDto);

    java.util.Optional<com.rev.app.entity.ArtistAccount> getArtistByEmail(String email);

    void saveArtist(com.rev.app.entity.ArtistAccount artist);

    void deleteArtist(int id);
}
