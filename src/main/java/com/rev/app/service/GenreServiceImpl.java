package com.rev.app.service;

import com.rev.app.dto.GenreRequestDto;
import com.rev.app.dto.GenreResponseDto;
import com.rev.app.entity.Genre;
import com.rev.app.mapper.GenreMapper;
import com.rev.app.repository.IGenreRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GenreServiceImpl implements IGenreService {

    private final IGenreRepository genreRepository;
    private final GenreMapper genreMapper;

    public GenreServiceImpl(IGenreRepository genreRepository, GenreMapper genreMapper) {
        this.genreRepository = genreRepository;
        this.genreMapper = genreMapper;
    }

    @Override
    public GenreResponseDto createGenre(GenreRequestDto requestDto) {
        Genre genre = genreMapper.toEntity(requestDto);
        genre = genreRepository.save(genre);
        return genreMapper.toResponseDto(genre);
    }

    @Override
    public GenreResponseDto getGenreById(int id) {
        return genreRepository.findById(id)
                .map(genreMapper::toResponseDto)
                .orElse(null);
    }

    @Override
    public List<GenreResponseDto> getAllGenres() {
        return genreRepository.findAll().stream()
                .map(genreMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
