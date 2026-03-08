package com.rev.app.rest;

import com.rev.app.dto.AlbumRequestDto;
import com.rev.app.dto.AlbumResponseDto;
import com.rev.app.service.IAlbumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AlbumRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IAlbumService albumService;

    @InjectMocks
    private AlbumRestController albumRestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(albumRestController).build();
    }

    @Test
    void testGetAllAlbums() throws Exception {
        AlbumResponseDto album = new AlbumResponseDto();
        album.setAlbumId(1);
        album.setTitle("Test Album");
        when(albumService.getAllAlbums()).thenReturn(Collections.singletonList(album));

        mockMvc.perform(get("/api/albums"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Album"));
    }

    @Test
    void testGetAlbumById_Found() throws Exception {
        AlbumResponseDto album = new AlbumResponseDto();
        album.setAlbumId(1);
        when(albumService.getAlbumById(1)).thenReturn(album);

        mockMvc.perform(get("/api/albums/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.albumId").value(1));
    }

    @Test
    void testDeleteAlbum() throws Exception {
        mockMvc.perform(delete("/api/albums/1"))
                .andExpect(status().isNoContent());

        verify(albumService).deleteAlbum(1);
    }
}
