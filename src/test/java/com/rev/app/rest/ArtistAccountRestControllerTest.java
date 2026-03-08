package com.rev.app.rest;

import com.rev.app.dto.ArtistAccountRequestDto;
import com.rev.app.dto.ArtistAccountResponseDto;
import com.rev.app.service.IArtistAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ArtistAccountRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IArtistAccountService artistService;

    @InjectMocks
    private ArtistAccountRestController artistRestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(artistRestController).build();
    }

    @Test
    void testGetAllArtists() throws Exception {
        ArtistAccountResponseDto artist = new ArtistAccountResponseDto();
        artist.setArtistId(1);
        artist.setStageName("Artist One");
        when(artistService.getAllArtists()).thenReturn(Collections.singletonList(artist));

        mockMvc.perform(get("/api/artists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stageName").value("Artist One"));
    }

    @Test
    void testGetArtistById_Found() throws Exception {
        ArtistAccountResponseDto artist = new ArtistAccountResponseDto();
        artist.setArtistId(1);
        when(artistService.getArtistById(1)).thenReturn(artist);

        mockMvc.perform(get("/api/artists/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.artistId").value(1));
    }
}
