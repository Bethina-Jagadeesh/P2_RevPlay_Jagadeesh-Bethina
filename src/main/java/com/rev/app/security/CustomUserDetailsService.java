package com.rev.app.security;

import com.rev.app.entity.ArtistAccount;
import com.rev.app.entity.UserAccount;
import com.rev.app.repository.IArtistAccountRepository;
import com.rev.app.repository.IUserAccountRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final IUserAccountRepository userRepository;
    private final IArtistAccountRepository artistRepository;

    public CustomUserDetailsService(IUserAccountRepository userRepository, IArtistAccountRepository artistRepository) {
        this.userRepository = userRepository;
        this.artistRepository = artistRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Get the login type from the request
        String type = null;
        try {
            var attributes = (org.springframework.web.context.request.ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                type = attributes.getRequest().getParameter("type");
            }
        } catch (Exception e) {
            // Not in a request context (e.g. JWT filter)
        }

        // Try to find in UserAccount table if type is listener or null
        if (type == null || "listener".equals(type)) {
            Optional<UserAccount> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                UserAccount user = userOpt.get();
                return new CustomUserDetails(user.getEmail(), user.getPasswordHash(), "ROLE_LISTENER");
            }
            if (type != null) {
                throw new UsernameNotFoundException("Listener not found with email: " + email);
            }
        }

        // Try to find in ArtistAccount table if type is artist or null
        if (type == null || "artist".equals(type)) {
            Optional<ArtistAccount> artistOpt = artistRepository.findByEmail(email);
            if (artistOpt.isPresent()) {
                ArtistAccount artist = artistOpt.get();
                return new CustomUserDetails(artist.getEmail(), artist.getPasswordHash(), "ROLE_ARTIST");
            }
            if (type != null) {
                throw new UsernameNotFoundException("Artist not found with email: " + email);
            }
        }

        throw new UsernameNotFoundException("User or Artist not found with email: " + email);
    }
}
