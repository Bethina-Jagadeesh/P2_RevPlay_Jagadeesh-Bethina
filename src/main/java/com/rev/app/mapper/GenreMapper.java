package com.rev.app.mapper;

import com.rev.app.dto.GenreRequestDto;
import com.rev.app.dto.GenreResponseDto;
import com.rev.app.entity.Genre;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper {

    public Genre toEntity(GenreRequestDto dto) {
        if (dto == null)
            return null;
        Genre entity = new Genre();
        entity.setGenreName(dto.getGenreName());
        return entity;
    }

    public GenreResponseDto toResponseDto(Genre entity) {
        if (entity == null)
            return null;
        return GenreResponseDto.builder()
                .genreId(entity.getGenreId())
                .genreName(entity.getGenreName())
                .build();
    }
}
