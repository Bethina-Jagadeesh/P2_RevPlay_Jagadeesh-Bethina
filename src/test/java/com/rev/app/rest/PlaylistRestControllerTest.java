package com.rev.app.rest;

import com.rev.app.dto.PlaylistResponseDto;
import com.rev.app.service.IPlaylistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PlaylistRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IPlaylistService playlistService;

    @InjectMocks
    private PlaylistRestController playlistRestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(playlistRestController).build();
    }

    @Test
    void testGetAllPlaylists() throws Exception {
        PlaylistResponseDto p = new PlaylistResponseDto();
        p.setPlaylistId(1);
        p.setName("My Playlist");
        when(playlistService.getAllPlaylists()).thenReturn(Collections.singletonList(p));

        mockMvc.perform(get("/api/playlists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("My Playlist"));
    }

    @Test
    void testAddSongToPlaylist() throws Exception {
        mockMvc.perform(post("/api/playlists/1/songs/101"))
                .andExpect(status().isOk());

        verify(playlistService).addSongToPlaylist(1, 101);
    }
}
