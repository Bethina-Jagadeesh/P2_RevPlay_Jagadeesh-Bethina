package com.rev.app.service;

import com.rev.app.dto.GenreRequestDto;
import com.rev.app.dto.GenreResponseDto;

import java.util.List;

public interface IGenreService {
    GenreResponseDto createGenre(GenreRequestDto requestDto);

    GenreResponseDto getGenreById(int id);

    List<GenreResponseDto> getAllGenres();
}
