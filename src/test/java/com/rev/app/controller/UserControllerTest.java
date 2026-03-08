package com.rev.app.controller;

import com.rev.app.repository.IUserAccountRepository;
import com.rev.app.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ISongService songService;
    @Mock
    private IFavoriteSongService favoriteSongService;
    @Mock
    private IPlaylistService playlistService;
    @Mock
    private IListeningHistoryService historyService;
    @Mock
    private IUserAccountService userService;
    @Mock
    private IUserAccountRepository userRepository;
    @Mock
    private IAlbumService albumService;
    @Mock
    private IArtistAccountService artistService;
    @Mock
    private IPodcastService podcastService;
    @Mock
    private IGenreService genreService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Resolver for @AuthenticationPrincipal UserDetails
        org.springframework.web.method.support.HandlerMethodArgumentResolver principalResolver = new org.springframework.web.method.support.HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(org.springframework.core.MethodParameter parameter) {
                return parameter.getParameterType()
                        .isAssignableFrom(org.springframework.security.core.userdetails.UserDetails.class);
            }

            @Override
            public Object resolveArgument(org.springframework.core.MethodParameter parameter,
                    org.springframework.web.method.support.ModelAndViewContainer mavContainer,
                    org.springframework.web.context.request.NativeWebRequest webRequest,
                    org.springframework.web.bind.support.WebDataBinderFactory binderFactory) {
                org.springframework.security.core.userdetails.UserDetails user = mock(
                        org.springframework.security.core.userdetails.UserDetails.class);
                when(user.getUsername()).thenReturn("user@test.com");
                java.util.Collection authorities = java.util.Collections.singletonList(
                        (org.springframework.security.core.GrantedAuthority) () -> "ROLE_USER");
                doReturn(authorities).when(user).getAuthorities();
                return user;
            }
        };

        org.springframework.web.servlet.view.InternalResourceViewResolver viewResolver = new org.springframework.web.servlet.view.InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        this.mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(principalResolver)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void testLibrary() throws Exception {
        mockMvc.perform(get("/user/library"))
                .andExpect(status().isOk())
                .andExpect(view().name("listener/library"));
    }

    @Test
    void testSearch() throws Exception {
        mockMvc.perform(get("/user/search").param("q", "test"))
                .andExpect(status().isOk())
                .andExpect(view().name("listener/search"));
    }
}
