package com.rev.app.service;

import com.rev.app.dto.ArtistAccountRequestDto;
import com.rev.app.dto.ArtistAccountResponseDto;
import com.rev.app.entity.ArtistAccount;
import com.rev.app.mapper.ArtistAccountMapper;
import com.rev.app.repository.IArtistAccountRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArtistAccountServiceImpl implements IArtistAccountService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ArtistAccountServiceImpl.class);

    private final IArtistAccountRepository artistRepository;
    private final ArtistAccountMapper artistAccountMapper;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public ArtistAccountServiceImpl(IArtistAccountRepository artistRepository,
            ArtistAccountMapper artistAccountMapper,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.artistRepository = artistRepository;
        this.artistAccountMapper = artistAccountMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public ArtistAccountResponseDto createArtist(ArtistAccountRequestDto requestDto) {
        if (artistRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            logger.warn("Artist registration failed: {} already exists.", requestDto.getEmail());
            throw new RuntimeException("Email already exists");
        }
        ArtistAccount artist = artistAccountMapper.toEntity(requestDto);
        artist.setPasswordHash(passwordEncoder.encode(requestDto.getPassword()));
        artist.setCreatedAt(java.time.LocalDateTime.now());

        if (requestDto.getSecurityAnswer() != null && !requestDto.getSecurityAnswer().isBlank()) {
            artist.setSecurityQuestion(requestDto.getSecurityQuestion());
            artist.setSecurityAnswerHash(passwordEncoder.encode(requestDto.getSecurityAnswer()));
        }

        artist = artistRepository.save(artist);
        logger.info("Successfully registered artist: {}", artist.getStageName());
        return artistAccountMapper.toResponseDto(artist);
    }

    @Override
    public ArtistAccountResponseDto getArtistById(int id) {
        return artistRepository.findById(id)
                .map(artistAccountMapper::toResponseDto)
                .orElse(null);
    }

    @Override
    public List<ArtistAccountResponseDto> getAllArtists() {
        return artistRepository.findAll().stream()
                .map(artistAccountMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ArtistAccountResponseDto updateArtist(int id, ArtistAccountRequestDto requestDto) {
        ArtistAccount existingArtist = artistRepository.findById(id).orElse(null);
        if (existingArtist != null) {
            existingArtist.setStageName(requestDto.getStageName());
            existingArtist.setBio(requestDto.getBio());
            existingArtist.setInstagramLink(requestDto.getInstagramLink());
            existingArtist.setTwitterLink(requestDto.getTwitterLink());
            existingArtist.setYoutubeLink(requestDto.getYoutubeLink());
            existingArtist.setSpotifyLink(requestDto.getSpotifyLink());
            artistRepository.save(existingArtist);
            return artistAccountMapper.toResponseDto(existingArtist);
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
