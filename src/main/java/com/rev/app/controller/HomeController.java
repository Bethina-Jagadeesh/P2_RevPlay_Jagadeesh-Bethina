package com.rev.app.controller;

import com.rev.app.dto.ArtistAccountRequestDto;
import com.rev.app.dto.UserAccountRequestDto;
import com.rev.app.entity.ArtistAccount;
import com.rev.app.entity.UserAccount;
import com.rev.app.repository.IArtistAccountRepository;
import com.rev.app.repository.IUserAccountRepository;
import com.rev.app.service.IArtistAccountService;
import com.rev.app.service.IUserAccountService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class HomeController {

    private final IUserAccountService userService;
    private final IArtistAccountService artistService;
    private final IUserAccountRepository userRepository;
    private final IArtistAccountRepository artistRepository;
    private final PasswordEncoder passwordEncoder;

    public HomeController(IUserAccountService userService,
            IArtistAccountService artistService,
            IUserAccountRepository userRepository,
            IArtistAccountRepository artistRepository,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.artistService = artistService;
        this.userRepository = userRepository;
        this.artistRepository = artistRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String home() {
        return "landing";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ARTIST"))) {
            return "redirect:/artist/dashboard";
        }
        return "redirect:/user/dashboard";
    }

    @GetMapping("/login")
    public String login() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/user/dashboard";
        }
        return "listener/login";
    }

    @GetMapping("/login/artist")
    public String loginArtist() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/artist/dashboard";
        }
        return "artist/login";
    }

    @GetMapping("/register/listener")
    public String registerListener(Model model) {
        model.addAttribute("title", "RevPlay - Listener Registration");
        return "listener/register";
    }

    @PostMapping("/register/listener")
    public String processRegisterListener(@ModelAttribute UserAccountRequestDto request) {
        userService.createUser(request);
        return "redirect:/?registered=true";
    }

    @GetMapping("/register/artist")
    public String registerArtist(Model model) {
        model.addAttribute("title", "RevPlay - Artist Registration");
        return "artist/register";
    }

    @PostMapping("/register/artist")
    public String processRegisterArtist(@ModelAttribute ArtistAccountRequestDto request) {
        artistService.createArtist(request);
        return "redirect:/?registered=true";
    }

    @GetMapping("/forgot-password/listener")
    public String forgotPasswordListener(Model model) {
        model.addAttribute("step", "1");
        return "listener/forgot_password";
    }

    @PostMapping("/forgot-password/listener/verify-email")
    public String verifyListenerEmail(@RequestParam String email, Model model) {
        Optional<UserAccount> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            model.addAttribute("step", "1");
            model.addAttribute("error", "No account found with that email address.");
            return "listener/forgot_password";
        }
        UserAccount user = userOpt.get();
        if (user.getSecurityQuestion() == null || user.getSecurityQuestion().isBlank()) {
            model.addAttribute("step", "1");
            model.addAttribute("error", "No security question set for this account. Please contact support.");
            return "listener/forgot_password";
        }
        model.addAttribute("step", "2");
        model.addAttribute("email", email);
        model.addAttribute("securityQuestion", user.getSecurityQuestion());
        return "listener/forgot_password";
    }

    @PostMapping("/forgot-password/listener/verify-answer")
    public String verifyListenerAnswer(@RequestParam String email,
            @RequestParam String answer,
            Model model) {
        Optional<UserAccount> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            model.addAttribute("step", "1");
            model.addAttribute("error", "Session expired. Please start again.");
            return "listener/forgot_password";
        }
        UserAccount user = userOpt.get();
        boolean correct = passwordEncoder.matches(answer, user.getSecurityAnswerHash());
        if (!correct) {
            model.addAttribute("step", "2");
            model.addAttribute("email", email);
            model.addAttribute("securityQuestion", user.getSecurityQuestion());
            model.addAttribute("error", "Incorrect answer. Please try again.");
            return "listener/forgot_password";
        }
        model.addAttribute("step", "3");
        model.addAttribute("email", email);
        return "listener/forgot_password";
    }

    @PostMapping("/forgot-password/listener/reset")
    public String resetListenerPassword(@RequestParam String email,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("step", "3");
            model.addAttribute("email", email);
            model.addAttribute("error", "Passwords do not match. Please try again.");
            return "listener/forgot_password";
        }
        Optional<UserAccount> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            model.addAttribute("step", "1");
            model.addAttribute("error", "Session expired. Please start again.");
            return "listener/forgot_password";
        }
        UserAccount user = userOpt.get();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return "redirect:/login?reset=true";
    }

    @GetMapping("/forgot-password/artist")
    public String forgotPasswordArtist(Model model) {
        model.addAttribute("step", "1");
        return "artist/forgot_password";
    }

    @PostMapping("/forgot-password/artist/verify-email")
    public String verifyArtistEmail(@RequestParam String email, Model model) {
        Optional<ArtistAccount> artistOpt = artistRepository.findByEmail(email);
        if (artistOpt.isEmpty()) {
            model.addAttribute("step", "1");
            model.addAttribute("error", "No artist account found with that email address.");
            return "artist/forgot_password";
        }
        String secQ = fetchArtistSecurityQuestion(email);
        if (secQ == null || secQ.isBlank()) {
            model.addAttribute("step", "1");
            model.addAttribute("error", "No security question set for this artist account. Please contact support.");
            return "artist/forgot_password";
        }
        model.addAttribute("step", "2");
        model.addAttribute("email", email);
        model.addAttribute("securityQuestion", secQ);
        return "artist/forgot_password";
    }

    @PostMapping("/forgot-password/artist/verify-answer")
    public String verifyArtistAnswer(@RequestParam String email,
            @RequestParam String answer,
            Model model) {
        Optional<ArtistAccount> artistOpt = artistRepository.findByEmail(email);
        if (artistOpt.isEmpty()) {
            model.addAttribute("step", "1");
            model.addAttribute("error", "Session expired. Please start again.");
            return "artist/forgot_password";
        }
        String secQ = fetchArtistSecurityQuestion(email);
        String secAHash = fetchArtistSecurityAnswerHash(email);
        boolean correct = secAHash != null && passwordEncoder.matches(answer, secAHash);
        if (!correct) {
            model.addAttribute("step", "2");
            model.addAttribute("email", email);
            model.addAttribute("securityQuestion", secQ);
            model.addAttribute("error", "Incorrect answer. Please try again.");
            return "artist/forgot_password";
        }
        model.addAttribute("step", "3");
        model.addAttribute("email", email);
        return "artist/forgot_password";
    }

    @PostMapping("/forgot-password/artist/reset")
    public String resetArtistPassword(@RequestParam String email,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("step", "3");
            model.addAttribute("email", email);
            model.addAttribute("error", "Passwords do not match. Please try again.");
            return "artist/forgot_password";
        }
        Optional<ArtistAccount> artistOpt = artistRepository.findByEmail(email);
        if (artistOpt.isEmpty()) {
            model.addAttribute("step", "1");
            model.addAttribute("error", "Session expired. Please start again.");
            return "artist/forgot_password";
        }
        ArtistAccount artist = artistOpt.get();
        artist.setPasswordHash(passwordEncoder.encode(newPassword));
        artistRepository.save(artist);
        return "redirect:/login/artist?reset=true";
    }

    private String fetchArtistSecurityQuestion(String email) {
        return artistRepository.findByEmail(email).map(ArtistAccount::getSecurityQuestion).orElse(null);
    }

    private String fetchArtistSecurityAnswerHash(String email) {
        return artistRepository.findByEmail(email).map(ArtistAccount::getSecurityAnswerHash).orElse(null);
    }
}
