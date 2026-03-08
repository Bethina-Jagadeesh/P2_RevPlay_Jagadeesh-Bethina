package com.rev.app.controller;

import com.rev.app.entity.ArtistAccount;
import com.rev.app.repository.IArtistAccountRepository;
import com.rev.app.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ArtistControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ISongService songService;
    @Mock
    private IAlbumService albumService;
    @Mock
    private IGenreService genreService;
    @Mock
    private IArtistAccountRepository artistRepository;
    @Mock
    private IPodcastService podcastService;
    @Mock
    private IFavoriteSongService favoriteSongService;
    @Mock
    private IListeningHistoryService historyService;

    @InjectMocks
    private ArtistController artistController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Resolver for @AuthenticationPrincipal UserDetails
        org.springframework.web.method.support.HandlerMethodArgumentResolver principalResolver = new org.springframework.web.method.support.HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(org.springframework.core.MethodParameter parameter) {
                return parameter.getParameterType().isAssignableFrom(UserDetails.class);
            }

            @Override
            public Object resolveArgument(org.springframework.core.MethodParameter parameter,
                    org.springframework.web.method.support.ModelAndViewContainer mavContainer,
                    org.springframework.web.context.request.NativeWebRequest webRequest,
                    org.springframework.web.bind.support.WebDataBinderFactory binderFactory) {
                UserDetails user = mock(UserDetails.class);
                when(user.getUsername()).thenReturn("artist@test.com");
                return user;
            }
        };

        org.springframework.web.servlet.view.InternalResourceViewResolver viewResolver = new org.springframework.web.servlet.view.InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        this.mockMvc = MockMvcBuilders.standaloneSetup(artistController)
                .setCustomArgumentResolvers(principalResolver)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void testUploadPage() throws Exception {
        mockMvc.perform(get("/artist/upload"))
                .andExpect(status().isOk())
                .andExpect(view().name("artist/upload"));
    }

    @Test
    void testDashboard() throws Exception {
        // Since we are using standaloneSetup and @AuthenticationPrincipal,
        // we might need to handle the principal if the method uses it heavily.
        // For simplicity in this generated test, we verify the view name.
        mockMvc.perform(get("/artist/dashboard"))
                .andExpect(view().name("artist/dashboard"));
    }
}
