package com.rev.app.controller;

import com.rev.app.entity.ArtistAccount;
import com.rev.app.entity.UserAccount;
import com.rev.app.repository.IArtistAccountRepository;
import com.rev.app.repository.IUserAccountRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final IArtistAccountRepository artistRepository;
    private final IUserAccountRepository userRepository;

    public GlobalControllerAdvice(IArtistAccountRepository artistRepository, IUserAccountRepository userRepository) {
        this.artistRepository = artistRepository;
        this.userRepository = userRepository;
    }

    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("isArtist")
    public boolean isArtist() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null)
            return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ARTIST"));
    }

    @ModelAttribute("email")
    public String email() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) auth.getPrincipal()).getUsername();
        }
        return null;
    }

    @ModelAttribute("profileImageUrl")
    public String profileImageUrl() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            String email = ((UserDetails) auth.getPrincipal()).getUsername();

            // Check artist first
            Optional<ArtistAccount> artist = artistRepository.findByEmail(email);
            if (artist.isPresent()) {
                return artist.get().getProfileImageUrl();
            }

            // Then check user
            Optional<UserAccount> user = userRepository.findByEmail(email);
            if (user.isPresent()) {
                return user.get().getProfileImageUrl();
            }
        }
        return null;
    }
}
