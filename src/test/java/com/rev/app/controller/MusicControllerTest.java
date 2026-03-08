package com.rev.app.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MusicControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MusicController musicController = new MusicController();
        org.springframework.web.servlet.view.InternalResourceViewResolver viewResolver = new org.springframework.web.servlet.view.InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");
        this.mockMvc = MockMvcBuilders.standaloneSetup(musicController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void testMusicPlayer() throws Exception {
        mockMvc.perform(get("/music"))
                .andExpect(status().isOk())
                .andExpect(view().name("music"))
                .andExpect(model().attribute("title", "RevPlay - Music Player"));
    }

    @Test
    void testMusicLibrary() throws Exception {
        mockMvc.perform(get("/music/library"))
                .andExpect(status().isOk())
                .andExpect(view().name("library"))
                .andExpect(model().attribute("title", "RevPlay - Library"));
    }
}
