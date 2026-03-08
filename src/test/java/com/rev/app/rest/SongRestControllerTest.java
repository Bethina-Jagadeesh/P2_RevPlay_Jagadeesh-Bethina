package com.rev.app.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rev.app.dto.SongResponseDto;
import com.rev.app.service.ISongService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SongRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ISongService songService;

    @InjectMocks
    private SongRestController songRestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(songRestController).build();
    }

    @Test
    void testGetAllSongs() throws Exception {
        // Arrange
        SongResponseDto song = new SongResponseDto();
        song.setSongId(1);
        song.setTitle("Test Song");
        when(songService.getAllSongs()).thenReturn(Collections.singletonList(song));

        // Act & Assert
        mockMvc.perform(get("/api/songs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Test Song"));

        verify(songService, times(1)).getAllSongs();
    }

    @Test
    void testGetSongById_Found() throws Exception {
        // Arrange
        SongResponseDto song = new SongResponseDto();
        song.setSongId(1);
        song.setTitle("Found Song");
        when(songService.getSongById(1)).thenReturn(song);

        // Act & Assert
        mockMvc.perform(get("/api/songs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Found Song"));
    }

    @Test
    void testGetSongById_NotFound() throws Exception {
        // Arrange
        when(songService.getSongById(999)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/songs/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testIncrementPlayCount() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/songs/1/play"))
                .andExpect(status().isOk());

        verify(songService, times(1)).incrementPlayCount(1);
    }
}
