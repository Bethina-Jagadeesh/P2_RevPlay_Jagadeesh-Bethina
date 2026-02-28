package com.rev.app.service;

import com.rev.app.dto.GenreRequestDto;
import com.rev.app.dto.GenreResponseDto;
import com.rev.app.entity.Genre;
import com.rev.app.mapper.IGenreMapper;
import com.rev.app.repository.IGenreRepository;
import org.springframework.stereotype.Service;
import com.rev.app.service.IGenreService;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GenreServiceImpl implements IGenreService {

    private final IGenreRepository genreRepository;

    public GenreServiceImpl(IGenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public GenreResponseDto createGenre(GenreRequestDto requestDto) {
        Genre genre = IGenreMapper.toEntity(requestDto);
        genre = genreRepository.save(genre);
        return IGenreMapper.toResponseDto(genre);
    }

    @Override
    public GenreResponseDto getGenreById(int id) {
        return genreRepository.findById(id)
                .map(IGenreMapper::toResponseDto)
                .orElse(null);
    }

    @Override
    public List<GenreResponseDto> getAllGenres() {
        return genreRepository.findAll().stream()
                .map(IGenreMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
