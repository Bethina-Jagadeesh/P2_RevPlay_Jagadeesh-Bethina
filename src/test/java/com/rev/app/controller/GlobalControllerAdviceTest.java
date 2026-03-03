package com.rev.app.controller;

import com.rev.app.repository.IArtistAccountRepository;
import com.rev.app.repository.IUserAccountRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class GlobalControllerAdviceTest {

    @Mock
    private IArtistAccountRepository artistRepository;
    @Mock
    private IUserAccountRepository userRepository;
    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private GlobalControllerAdvice advice;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCurrentUri() {
        when(request.getRequestURI()).thenReturn("/test");
        assertEquals("/test", advice.currentUri(request));
    }
}
