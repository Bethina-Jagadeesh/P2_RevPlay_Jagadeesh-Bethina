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
import com.rev.app.service.IFavoriteSongService;
import com.rev.app.service.IListeningHistoryService;
import com.rev.app.dto.ListeningHistoryResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(ArtistController.class);

    private final ISongService songService;
    private final IAlbumService albumService;
    private final IGenreService genreService;
    private final IArtistAccountRepository artistRepository;
    private final IPodcastService podcastService;
    private final IFavoriteSongService favoriteSongService;
    private final IListeningHistoryService historyService;
    private final com.rev.app.repository.IUserAccountRepository userRepository;

    public ArtistController(ISongService songService,
            IAlbumService albumService,
            IGenreService genreService,
            IArtistAccountRepository artistRepository,
            IPodcastService podcastService,
            IFavoriteSongService favoriteSongService,
            IListeningHistoryService historyService,
            com.rev.app.repository.IUserAccountRepository userRepository) {
        this.songService = songService;
        this.albumService = albumService;
        this.genreService = genreService;
        this.artistRepository = artistRepository;
        this.podcastService = podcastService;
        this.favoriteSongService = favoriteSongService;
        this.historyService = historyService;
        this.userRepository = userRepository;
    }

    private void markFavorites(List<com.rev.app.dto.SongResponseDto> songs, int userId) {
        if (userId <= 0 || songs == null || songs.isEmpty())
            return;
        List<com.rev.app.dto.FavoriteSongResponseDto> favorites = favoriteSongService.getFavoritesByUser(userId);
        java.util.Set<Integer> favoriteSongIds = favorites.stream()
                .map(com.rev.app.dto.FavoriteSongResponseDto::getSongId)
                .collect(java.util.stream.Collectors.toSet());
        for (com.rev.app.dto.SongResponseDto song : songs) {
            song.setFavorite(favoriteSongIds.contains(song.getSongId()));
        }
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
            List<SongResponseDto> artistSongs = songService.getSongsByArtistId(artist.getArtistId());

            // Fetch Top Songs (10 most listened to)
            List<SongResponseDto> topSongs = artistSongs.stream()
                    .sorted((s1, s2) -> Integer.compare(s2.getPlayCount(), s1.getPlayCount()))
                    .limit(10)
                    .collect(java.util.stream.Collectors.toList());

            // Fetch Recent History
            List<ListeningHistoryResponseDto> history = historyService.getHistoryByUser(artist.getArtistId());
            List<SongResponseDto> recentPlayed = history.stream()
                    .map(h -> songService.getSongById(h.getSongId()))
                    .filter(java.util.Objects::nonNull)
                    .distinct() // Show unique songs
                    .limit(8)
                    .collect(java.util.stream.Collectors.toList());

            int userId = artist.getArtistId();
            // Check if there's a UserAccount with this email to get the correct listener ID
            Optional<com.rev.app.entity.UserAccount> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                userId = userOpt.get().getUserId();
            }

            markFavorites(topSongs, userId);
            markFavorites(recentPlayed, userId);

            model.addAttribute("topSongs", topSongs);
            model.addAttribute("recentPlayed", recentPlayed);
            model.addAttribute("artist", artist);
            model.addAttribute("name", artist.getStageName());
            model.addAttribute("profileImageUrl", artist.getProfileImageUrl());
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

                    request.setFileUrl("/uploads/songs/" + fileName);
                } catch (Exception e) {
                    e.printStackTrace();
                    return "redirect:/artist/upload?error=upload_failed";
                }
            }

            if (request.getDurationSeconds() <= 0 && !songFile.isEmpty()) {
                // Approximate 128kbps (16000 bytes per second)
                int dur = (int) (songFile.getSize() / 16000);
                request.setDurationSeconds(Math.max(1, dur));
            }
            if (request.getGenreId() <= 0)
                request.setGenreId(1);
            songService.createSong(request);
            logger.info("Artist {} uploaded song: {}", artist.getStageName(), request.getTitle());
        }
        return "redirect:/artist/dashboard?uploaded=true";
    }

    @PostMapping("/songs/{id}/delete")
    public String deleteSong(@PathVariable int id) {
        songService.deleteSong(id);
        logger.info("Song deleted by artist, ID: {}", id);
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
            logger.info("Artist {} created album: {}", artist.getStageName(), request.getTitle());
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
        int userId = -1;
        Optional<com.rev.app.entity.UserAccount> userOpt = userRepository.findByEmail(userDetails.getUsername());
        if (userOpt.isPresent()) {
            userId = userOpt.get().getUserId();
        } else {
            artistRepository.findByEmail(userDetails.getUsername()).ifPresent(a -> {
                // Not the best practice but consistent with resolveUserId pattern
            });
            // We can assume userOpt is usually present for login, or just use -1
        }
        markFavorites(songs, userId);

        model.addAttribute("songs", songs);
        model.addAttribute("albumId", id);
        model.addAttribute("isArtist", true);
        model.addAttribute("title", "Album Songs - RevPlay");
        model.addAttribute("email", userDetails.getUsername());
        return "artist/album_songs";
    }

    @GetMapping("/analytics")
    public String analytics(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Optional<ArtistAccount> artistOpt = currentArtist(userDetails);
        if (artistOpt.isPresent()) {
            ArtistAccount artist = artistOpt.get();
            List<SongResponseDto> songs = songService.getSongsByArtistId(artist.getArtistId());

            int userId = artist.getArtistId();
            Optional<com.rev.app.entity.UserAccount> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                userId = userOpt.get().getUserId();
            }
            markFavorites(songs, userId);
            int totalPlays = songs.stream().mapToInt(SongResponseDto::getPlayCount).sum();

            List<Integer> songIds = songs.stream().map(SongResponseDto::getSongId).toList();
            long totalFavorites = favoriteSongService.getFavoriteCountForSongs(songIds);

            List<SongResponseDto> topSongs = songs.stream()
                    .sorted((a, b) -> b.getPlayCount() - a.getPlayCount())
                    .limit(10)
                    .toList();

            List<com.rev.app.dto.UserAccountResponseDto> favoritedBy = favoriteSongService
                    .getUsersWhoFavoritedSongs(songIds);
            java.util.Map<java.time.LocalDate, com.rev.app.dto.DailyTrendDto> trends = historyService
                    .getListeningTrends(songIds);
            List<com.rev.app.dto.UserPlayCountDto> topListeners = historyService.getTopListeners(songIds);

            model.addAttribute("songs", songs);
            model.addAttribute("topSongs", topSongs);
            model.addAttribute("favoritedBy", favoritedBy);
            model.addAttribute("trends", trends);
            model.addAttribute("topListeners", topListeners);

            model.addAttribute("totalSongs", songs.size());
            model.addAttribute("totalPlays", totalPlays);
            model.addAttribute("totalFavorites", totalFavorites);
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
            request.setHostName(artist.getStageName() != null ? artist.getStageName() : artist.getEmail());

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

                    request.setFileUrl("/uploads/podcasts/" + fileName);

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
