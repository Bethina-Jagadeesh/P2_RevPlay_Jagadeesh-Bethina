package com.rev.app.service;

import com.rev.app.dto.ArtistAccountRequestDto;
import com.rev.app.dto.ArtistAccountResponseDto;
import com.rev.app.entity.ArtistAccount;
import com.rev.app.mapper.IArtistAccountMapper;
import com.rev.app.repository.IArtistAccountRepository;
import org.springframework.stereotype.Service;
import com.rev.app.service.IArtistAccountService;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArtistAccountServiceImpl implements IArtistAccountService {

    private final IArtistAccountRepository artistRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public ArtistAccountServiceImpl(IArtistAccountRepository artistRepository,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.artistRepository = artistRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public ArtistAccountResponseDto createArtist(ArtistAccountRequestDto requestDto) {
        ArtistAccount artist = IArtistAccountMapper.toEntity(requestDto);
        artist.setPasswordHash(passwordEncoder.encode(requestDto.getPassword()));

        if (requestDto.getSecurityAnswer() != null && !requestDto.getSecurityAnswer().isBlank()) {
            artist.setSecurityQuestion(requestDto.getSecurityQuestion());
            artist.setSecurityAnswerHash(passwordEncoder.encode(requestDto.getSecurityAnswer()));
        }

        artist = artistRepository.save(artist);
        return IArtistAccountMapper.toResponseDto(artist);
    }

    @Override
    public ArtistAccountResponseDto getArtistById(int id) {
        return artistRepository.findById(id)
                .map(IArtistAccountMapper::toResponseDto)
                .orElse(null);
    }

    @Override
    public List<ArtistAccountResponseDto> getAllArtists() {
        return artistRepository.findAll().stream()
                .map(IArtistAccountMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ArtistAccountResponseDto updateArtist(int id, ArtistAccountRequestDto requestDto) {
        ArtistAccount existingArtist = artistRepository.findById(id).orElse(null);
        if (existingArtist != null) {
            existingArtist.setStageName(requestDto.getStageName());
            existingArtist.setBio(requestDto.getBio());
            artistRepository.save(existingArtist);
            return IArtistAccountMapper.toResponseDto(existingArtist);
        }
        return null;
    }

    @Override
    public java.util.Optional<ArtistAccount> getArtistByEmail(String email) {
        return artistRepository.findByEmail(email);
    }

    @Override
    public void saveArtist(ArtistAccount artist) {
        artistRepository.save(artist);
    }

    @Override
    public void deleteArtist(int id) {
        artistRepository.deleteById(id);
    }
}
