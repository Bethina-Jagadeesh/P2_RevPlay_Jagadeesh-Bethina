package com.rev.app.controller;

import com.rev.app.dto.AlbumRequestDto;
import com.rev.app.dto.AlbumResponseDto;
import com.rev.app.dto.SongRequestDto;
import com.rev.app.dto.SongResponseDto;
import com.rev.app.dto.GenreResponseDto;
import com.rev.app.entity.ArtistAccount;
import com.rev.app.repository.IArtistAccountRepository;
import com.rev.app.service.IAlbumService;
import com.rev.app.service.ISongService;
import com.rev.app.service.IGenreService;
import com.rev.app.service.IPodcastService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/artist")
public class ArtistController {

    private final ISongService songService;
    private final IAlbumService albumService;
    private final IGenreService genreService;
    private final IArtistAccountRepository artistRepository;
    private final IPodcastService podcastService;

    public ArtistController(ISongService songService,
                            IAlbumService albumService,
                            IGenreService genreService,
                            IArtistAccountRepository artistRepository,
                            IPodcastService podcastService) {
        this.songService = songService;
        this.albumService = albumService;
        this.genreService = genreService;
        this.artistRepository = artistRepository;
        this.podcastService = podcastService;
    }

    private Optional<ArtistAccount> currentArtist(UserDetails userDetails) {
        return artistRepository.findByEmail(userDetails.getUsername());
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Optional<ArtistAccount> artistOpt = currentArtist(userDetails);

        if (artistOpt.isPresent()) {
            ArtistAccount artist = artistOpt.get();
            List<SongResponseDto> songs = songService.getSongsByArtistId(artist.getArtistId());
            int totalPlays = songs.stream().mapToInt(SongResponseDto::getPlayCount).sum();
            model.addAttribute("songs", songs);
            model.addAttribute("artist", artist);
            model.addAttribute("totalSongs", songs.size());
            model.addAttribute("totalPlays", totalPlays);
            model.addAttribute("isArtist", true);
        }

        model.addAttribute("title", "Artist Dashboard - RevPlay");
        model.addAttribute("email", email);
        return "artist/dashboard";
    }

    @GetMapping("/upload")
    public String uploadPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        List<GenreResponseDto> genres = genreService.getAllGenres();
        List<AlbumResponseDto> albums = List.of();
        Optional<ArtistAccount> artistOpt = currentArtist(userDetails);
        if (artistOpt.isPresent()) {
            albums = albumService.getAlbumsByArtistId(artistOpt.get().getArtistId());
        }
        model.addAttribute("genres", genres);
        model.addAttribute("albums", albums);
        model.addAttribute("isArtist", true);
        model.addAttribute("title", "Upload Music - RevPlay");
        model.addAttribute("songRequest", new SongRequestDto());
        return "artist/upload";
    }

    @PostMapping("/upload")
    public String processUpload(@ModelAttribute SongRequestDto request,
                                @RequestParam("songFile") org.springframework.web.multipart.MultipartFile songFile,
                                @AuthenticationPrincipal UserDetails userDetails) {
        Optional<ArtistAccount> artistOpt = currentArtist(userDetails);
        if (artistOpt.isPresent()) {
            ArtistAccount artist = artistOpt.get();
            request.setArtistId(artist.getArtistId());

            if (!songFile.isEmpty()) {
                try {
                    String fileName = System.currentTimeMillis() + "_" + songFile.getOriginalFilename();
                    String uploadDir = "src/main/resources/static/uploads/songs/";
                    java.io.File dir = new java.io.File(uploadDir);
                    if (!dir.exists())
                        dir.mkdirs();

                    java.nio.file.Path path = java.nio.file.Paths.get(uploadDir + fileName);
                    java.nio.file.Files.copy(songFile.getInputStream(), path,
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                    request.setFileUrl("/static/css/uploads/songs/" + fileName);
                } catch (Exception e) {
                    e.printStackTrace();
                    return "redirect:/artist/upload?error=upload_failed";
                }
            }

            if (request.getDurationSeconds() <= 0)
                request.setDurationSeconds(180);
            if (request.getGenreId() <= 0)
                request.setGenreId(1);
            songService.createSong(request);
        }
        return "redirect:/artist/dashboard?uploaded=true";
    }

    @PostMapping("/songs/{id}/delete")
    public String deleteSong(@PathVariable int id) {
        songService.deleteSong(id);
        return "redirect:/artist/dashboard?deleted=true";
    }

    @GetMapping("/albums")
    public String albums(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<ArtistAccount> artistOpt = currentArtist(userDetails);
        List<AlbumResponseDto> albums = List.of();
        if (artistOpt.isPresent()) {
            albums = albumService.getAlbumsByArtistId(artistOpt.get().getArtistId());
        }
        model.addAttribute("albums", albums);
        model.addAttribute("albumRequest", new AlbumRequestDto());
        model.addAttribute("isArtist", true);
        model.addAttribute("title", "My Albums - RevPlay");
        model.addAttribute("email", userDetails.getUsername());
        return "artist/albums";
    }

    @PostMapping("/albums/create")
    public String createAlbum(@ModelAttribute AlbumRequestDto request,
                              @AuthenticationPrincipal UserDetails userDetails) {
        Optional<ArtistAccount> artistOpt = currentArtist(userDetails);
        artistOpt.ifPresent(artist -> {
            request.setArtistId(artist.getArtistId());
            albumService.createAlbum(request);
        });
        return "redirect:/artist/albums?created=true";
    }

    @PostMapping("/albums/{id}/delete")
    public String deleteAlbum(@PathVariable int id) {
        albumService.deleteAlbum(id);
        return "redirect:/artist/albums?deleted=true";
    }

    @GetMapping("/albums/{id}/songs")
    public String albumSongs(@PathVariable int id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        List<SongResponseDto> songs = songService.getSongsByAlbumId(id);
        model.addAttribute("songs", songs);
        model.addAttribute("albumId", id);
        model.addAttribute("isArtist", true);
        model.addAttribute("title", "Album Songs - RevPlay");
        model.addAttribute("email", userDetails.getUsername());
        return "artist/album_songs";
    }

    @GetMapping("/analytics")
    public String analytics(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<ArtistAccount> artistOpt = currentArtist(userDetails);
        if (artistOpt.isPresent()) {
            ArtistAccount artist = artistOpt.get();
            List<SongResponseDto> songs = songService.getSongsByArtistId(artist.getArtistId());
            int totalPlays = songs.stream().mapToInt(SongResponseDto::getPlayCount).sum();
            List<SongResponseDto> topSongs = songs.stream()
                    .sorted((a, b) -> b.getPlayCount() - a.getPlayCount())
                    .limit(10)
                    .toList();
            model.addAttribute("songs", songs);
            model.addAttribute("topSongs", topSongs);
            model.addAttribute("totalSongs", songs.size());
            model.addAttribute("totalPlays", totalPlays);
            model.addAttribute("artist", artist);
            model.addAttribute("isArtist", true);
        }
        model.addAttribute("title", "Analytics - RevPlay");
        model.addAttribute("email", userDetails.getUsername());
        return "artist/analytics";
    }

    @GetMapping("/my-songs")
    public String mySongs(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<ArtistAccount> artistOpt = currentArtist(userDetails);
        if (artistOpt.isPresent()) {
            List<SongResponseDto> songs = songService.getSongsByArtistId(artistOpt.get().getArtistId());
            model.addAttribute("songs", songs);
            model.addAttribute("isArtist", true);
        }
        model.addAttribute("title", "My Songs - RevPlay");
        model.addAttribute("email", userDetails.getUsername());
        return "artist/songs";
    }

    @GetMapping("/my-podcasts")
    public String myPodcasts(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<ArtistAccount> artistOpt = currentArtist(userDetails);
        if (artistOpt.isPresent()) {
            List<com.rev.app.dto.PodcastResponseDto> podcasts = podcastService
                    .getPodcastsByArtistId(artistOpt.get().getArtistId());
            model.addAttribute("podcasts", podcasts);
            model.addAttribute("isArtist", true);
        }
        model.addAttribute("title", "My Podcasts - RevPlay");
        model.addAttribute("email", userDetails.getUsername());
        return "artist/podcasts";
    }

    @PostMapping("/podcasts/create")
    public String createPodcast(@ModelAttribute com.rev.app.dto.PodcastRequestDto request,
                                @RequestParam("podcastFile") org.springframework.web.multipart.MultipartFile podcastFile,
                                @AuthenticationPrincipal UserDetails userDetails) {

        if (podcastFile.getSize() > 50 * 1024 * 1024) {
            return "redirect:/artist/my-podcasts?error=file_too_large";
        }

        Optional<ArtistAccount> artistOpt = currentArtist(userDetails);
        if (artistOpt.isPresent()) {
            ArtistAccount artist = artistOpt.get();
            request.setArtistId(artist.getArtistId());

            if (!podcastFile.isEmpty()) {
                try {
                    String fileName = System.currentTimeMillis() + "_" + podcastFile.getOriginalFilename();
                    String uploadDir = "src/main/resources/static/uploads/podcasts/";
                    java.io.File dir = new java.io.File(uploadDir);
                    if (!dir.exists())
                        dir.mkdirs();

                    java.nio.file.Path path = java.nio.file.Paths.get(uploadDir + fileName);
                    java.nio.file.Files.copy(podcastFile.getInputStream(), path,
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                } catch (Exception e) {
                    e.printStackTrace();
                    return "redirect:/artist/my-podcasts?error=upload_failed";
                }
            }
            podcastService.createPodcast(request);
        }
        return "redirect:/artist/my-podcasts?created=true";
    }
}
