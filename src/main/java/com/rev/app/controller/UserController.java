package com.rev.app.controller;

import com.rev.app.dto.*;
import com.rev.app.entity.ArtistAccount;
import com.rev.app.entity.UserAccount;
import com.rev.app.repository.IUserAccountRepository;
import com.rev.app.service.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    private final ISongService songService;
    private final IFavoriteSongService favoriteSongService;
    private final IPlaylistService playlistService;
    private final IListeningHistoryService historyService;
    private final IUserAccountService userService;
    private final IUserAccountRepository userRepository;
    private final IAlbumService albumService;
    private final IArtistAccountService artistService;
    private final IPodcastService podcastService;
    private final IGenreService genreService;

    public UserController(ISongService songService,
                          IFavoriteSongService favoriteSongService,
                          IPlaylistService playlistService,
                          IListeningHistoryService historyService,
                          IUserAccountService userService,
                          IUserAccountRepository userRepository,
                          IAlbumService albumService,
                          IArtistAccountService artistService,
                          IPodcastService podcastService,
                          IGenreService genreService) {
        this.songService = songService;
        this.favoriteSongService = favoriteSongService;
        this.playlistService = playlistService;
        this.historyService = historyService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.albumService = albumService;
        this.artistService = artistService;
        this.podcastService = podcastService;
        this.genreService = genreService;
    }

    private int resolveUserId(UserDetails userDetails) {
        String email = userDetails.getUsername();
        Optional<UserAccount> user = userRepository.findByEmail(email);
        if (user.isPresent())
            return user.get().getUserId();

        // Check if it's an Artist instead via service
        Optional<ArtistAccount> artist = artistService.getArtistByEmail(email);
        return artist.map(ArtistAccount::getArtistId).orElse(-1);
    }

    private void markFavorites(List<SongResponseDto> songs, int userId) {
        if (userId <= 0)
            return;
        List<FavoriteSongResponseDto> favorites = favoriteSongService.getFavoritesByUser(userId);
        java.util.Set<Integer> favoriteSongIds = favorites.stream()
                .map(FavoriteSongResponseDto::getSongId)
                .collect(java.util.stream.Collectors.toSet());
        for (SongResponseDto song : songs) {
            song.setFavorite(favoriteSongIds.contains(song.getSongId()));
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        List<SongResponseDto> songs = songService.getAllSongs();
        int userId = resolveUserId(userDetails);
        markFavorites(songs, userId);
        boolean isArtist = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ARTIST"));
        model.addAttribute("songs", songs);
        model.addAttribute("isArtist", isArtist);
        model.addAttribute("title", "RevPlay - Dashboard");
        model.addAttribute("email", userDetails.getUsername());
        return "dashboard/home";
    }

    @GetMapping("/library")
    public String library(Model model,
                          @RequestParam(required = false, defaultValue = "0") int genreId,
                          @AuthenticationPrincipal UserDetails userDetails) {
        List<SongResponseDto> songs = genreId > 0
                ? songService.getSongsByGenreId(genreId)
                : songService.getAllSongs();
        int userId = resolveUserId(userDetails);
        markFavorites(songs, userId);
        boolean isArtist = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ARTIST"));
        model.addAttribute("isArtist", isArtist);
        model.addAttribute("songs", songs);
        model.addAttribute("selectedGenre", genreId);
        model.addAttribute("title", "RevPlay - Library");
        model.addAttribute("email", userDetails.getUsername());
        return "listener/library";
    }

    @GetMapping("/search")
    public String search(Model model,
                         @RequestParam(required = false, defaultValue = "") String q,
                         @AuthenticationPrincipal UserDetails userDetails) {
        List<SongResponseDto> results = q.isEmpty() ? List.of() : songService.searchSongs(q);
        int userId = resolveUserId(userDetails);
        markFavorites(results, userId);
        boolean isArtist = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ARTIST"));
        model.addAttribute("isArtist", isArtist);
        model.addAttribute("songs", results);
        model.addAttribute("query", q);
        model.addAttribute("title", "RevPlay - Search");
        model.addAttribute("email", userDetails.getUsername());
        return "listener/search";
    }

    @GetMapping("/favorites")
    public String favorites(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        int userId = resolveUserId(userDetails);
        List<FavoriteSongResponseDto> favorites = favoriteSongService.getFavoritesByUser(userId);

        List<SongResponseDto> songs = favorites.stream()
                .map(f -> {
                    SongResponseDto s = songService.getSongById(f.getSongId());
                    if (s != null)
                        s.setFavorite(true);
                    return s;
                })
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toList());

        boolean isArtist = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ARTIST"));
        model.addAttribute("isArtist", isArtist);
        model.addAttribute("songs", songs);
        model.addAttribute("title", "RevPlay - Liked Songs");
        model.addAttribute("email", userDetails.getUsername());
        return "listener/favorites";
    }

    @PostMapping("/favorites/add/{songId}")
    public String addFavorite(@PathVariable int songId,
                              @AuthenticationPrincipal UserDetails userDetails,
                              @RequestHeader(value = "Referer", defaultValue = "/user/dashboard") String referer) {
        int userId = resolveUserId(userDetails);
        FavoriteSongRequestDto dto = new FavoriteSongRequestDto(userId, songId);
        favoriteSongService.addFavorite(dto);
        return "redirect:" + referer;
    }

    @PostMapping("/favorites/remove/{songId}")
    public String removeFavorite(@PathVariable int songId,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 @RequestHeader(value = "Referer", defaultValue = "/user/favorites") String referer) {
        int userId = resolveUserId(userDetails);
        favoriteSongService.removeFavorite(userId, songId);
        return "redirect:" + referer;
    }

    @PostMapping("/favorites/toggle/{songId}")
    @ResponseBody
    public java.util.Map<String, Object> toggleFavorite(@PathVariable int songId,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        int userId = resolveUserId(userDetails);
        boolean liked = favoriteSongService.toggleFavorite(userId, songId);
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("liked", liked);
        return response;
    }

    @PostMapping("/play/{songId}")
    @ResponseBody
    public SongResponseDto playSong(@PathVariable int songId,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        int userId = resolveUserId(userDetails);
        songService.incrementPlayCount(songId);
        ListeningHistoryRequestDto historyDto = new ListeningHistoryRequestDto(userId, songId, "play");
        historyService.recordHistory(historyDto);
        return songService.getSongById(songId);
    }

    @GetMapping("/playlists")
    public String playlists(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        int userId = resolveUserId(userDetails);
        List<PlaylistResponseDto> myPlaylists = playlistService.getPlaylistsByUserId(userId);
        List<SongResponseDto> allSongs = songService.getAllSongs();
        model.addAttribute("playlists", myPlaylists);
        model.addAttribute("allSongs", allSongs);
        boolean isArtist = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ARTIST"));
        model.addAttribute("isArtist", isArtist);
        model.addAttribute("newPlaylist", new PlaylistRequestDto());
        model.addAttribute("title", "RevPlay - Playlists");
        model.addAttribute("email", userDetails.getUsername());
        return "listener/playlists";
    }

    @PostMapping("/playlists/create")
    public String createPlaylist(@ModelAttribute PlaylistRequestDto request,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        int userId = resolveUserId(userDetails);
        request.setUserId(userId);
        playlistService.createPlaylist(request);
        return "redirect:/user/playlists?created=true";
    }

    @PostMapping("/playlists/{id}/delete")
    public String deletePlaylist(@PathVariable int id) {
        playlistService.deletePlaylist(id);
        return "redirect:/user/playlists?deleted=true";
    }

    @GetMapping("/playlists/{id}")
    public String playlistDetail(@PathVariable int id, Model model,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        PlaylistResponseDto playlist = playlistService.getPlaylistById(id);
        List<Integer> songIds = playlistService.getSongIdsInPlaylist(id);
        List<SongResponseDto> songsInPlaylist = songIds.stream()
                .map(songService::getSongById)
                .filter(s -> s != null)
                .toList();
        List<SongResponseDto> allSongs = songService.getAllSongs();
        boolean isArtist = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ARTIST"));
        model.addAttribute("isArtist", isArtist);
        model.addAttribute("playlist", playlist);
        model.addAttribute("songsInPlaylist", songsInPlaylist);
        model.addAttribute("allSongs", allSongs);
        model.addAttribute("title", "RevPlay - " + (playlist != null ? playlist.getName() : "Playlist"));
        model.addAttribute("email", userDetails.getUsername());
        return "listener/playlist_detail";
    }

    @PostMapping("/playlists/{id}/songs/add/{songId}")
    public String addSongToPlaylist(@PathVariable int id, @PathVariable int songId) {
        playlistService.addSongToPlaylist(id, songId);
        return "redirect:/user/playlists/" + id;
    }

    @PostMapping("/playlists/{id}/songs/remove/{songId}")
    public String removeSongFromPlaylist(@PathVariable int id, @PathVariable int songId) {
        playlistService.removeSongFromPlaylist(id, songId);
        return "redirect:/user/playlists/" + id;
    }

    @GetMapping("/history")
    public String history(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        int userId = resolveUserId(userDetails);
        List<ListeningHistoryResponseDto> history = historyService.getHistoryByUser(userId);
        boolean isArtist = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ARTIST"));
        model.addAttribute("isArtist", isArtist);
        model.addAttribute("history", history);
        model.addAttribute("title", "RevPlay - Listening History");
        model.addAttribute("email", userDetails.getUsername());
        return "listener/history";
    }

    @PostMapping("/history/clear")
    public String clearHistory(@AuthenticationPrincipal UserDetails userDetails) {
        int userId = resolveUserId(userDetails);
        historyService.clearHistory(userId);
        return "redirect:/user/history?cleared=true";
    }

    @GetMapping("/profile")
    public String profile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        boolean isArtist = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ARTIST"));

        if (isArtist) {
            Optional<ArtistAccount> artistOpt = artistService.getArtistByEmail(email);
            if (artistOpt.isPresent()) {
                ArtistAccount artist = artistOpt.get();
                List<SongResponseDto> songs = songService.getSongsByArtistId(artist.getArtistId());
                model.addAttribute("user", artist);
                model.addAttribute("totalSongs", songs.size());
                model.addAttribute("totalPlays", songs.stream().mapToInt(SongResponseDto::getPlayCount).sum());
            }
        } else {
            int userId = resolveUserId(userDetails);
            UserAccountResponseDto userDto = userService.getUserByEmail(email);
            int totalPlaylists = playlistService.getPlaylistsByUserId(userId).size();
            int totalFavorites = favoriteSongService.getFavoritesByUser(userId).size();
            model.addAttribute("user", userDto);
            model.addAttribute("totalPlaylists", totalPlaylists);
            model.addAttribute("totalFavorites", totalFavorites);
        }

        model.addAttribute("isArtist", isArtist);
        model.addAttribute("title", "RevPlay - Profile");
        model.addAttribute("email", email);
        return "listener/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam("fullName") String fullName,
                                @RequestParam(value = "bio", required = false) String bio,
                                @RequestParam(value = "profilePhoto", required = false) org.springframework.web.multipart.MultipartFile file,
                                @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        boolean isArtist = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ARTIST"));

        String photoUrl = null;
        if (file != null && !file.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                String uploadDir = "src/main/resources/static/uploads/profiles/";
                java.io.File dir = new java.io.File(uploadDir);
                if (!dir.exists())
                    dir.mkdirs();
                java.nio.file.Path path = java.nio.file.Paths.get(uploadDir + fileName);
                java.nio.file.Files.copy(file.getInputStream(), path,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                photoUrl = "/static/css/uploads/profiles/" + fileName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (isArtist) {
            Optional<ArtistAccount> artistOpt = artistService.getArtistByEmail(email);
            if (artistOpt.isPresent()) {
                ArtistAccount artist = artistOpt.get();
                artist.setStageName(fullName);
                if (bio != null)
                    artist.setBio(bio);
                if (photoUrl != null)
                    artist.setProfileImageUrl(photoUrl);
                artistService.saveArtist(artist);
            }
        } else {
            Optional<UserAccount> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                UserAccount user = userOpt.get();
                user.setFullName(fullName);
                if (photoUrl != null)
                    user.setProfileImageUrl(photoUrl);
                userRepository.save(user);
            }
        }

        return "redirect:/user/profile?updated=true";
    }

    @GetMapping("/browse-albums")
    public String browseAlbums(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        List<AlbumResponseDto> albums = albumService.getAllAlbums();
        boolean isArtist = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ARTIST"));
        model.addAttribute("albums", albums);
        model.addAttribute("isArtist", isArtist);
        model.addAttribute("title", "RevPlay - Browse Albums");
        model.addAttribute("email", userDetails.getUsername());
        return "listener/browse_albums";
    }

    @GetMapping("/browse-artists")
    public String browseArtists(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        List<ArtistAccountResponseDto> artists = artistService.getAllArtists();
        boolean isArtist = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ARTIST"));
        model.addAttribute("artists", artists);
        model.addAttribute("isArtist", isArtist);
        model.addAttribute("title", "RevPlay - Browse Artists");
        model.addAttribute("email", userDetails.getUsername());
        return "listener/browse_artists";
    }

    @GetMapping("/browse-podcasts")
    public String browsePodcasts(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        List<PodcastResponseDto> podcasts = podcastService.getAllPodcasts();
        boolean isArtist = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ARTIST"));
        model.addAttribute("podcasts", podcasts);
        model.addAttribute("isArtist", isArtist);
        model.addAttribute("title", "RevPlay - Browse Podcasts");
        model.addAttribute("email", userDetails.getUsername());
        return "listener/browse_podcasts";
    }

    @GetMapping("/browse-genres")
    public String browseGenres(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        List<GenreResponseDto> genres = genreService.getAllGenres();
        boolean isArtist = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ARTIST"));
        model.addAttribute("genres", genres);
        model.addAttribute("isArtist", isArtist);
        model.addAttribute("title", "RevPlay - Browse Genres");
        model.addAttribute("email", userDetails.getUsername());
        return "listener/browse_genres";
    }

    @GetMapping("/albums/{id}/songs")
    public String viewAlbumSongs(@PathVariable int id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        AlbumResponseDto album = albumService.getAlbumById(id);
        List<SongResponseDto> songs = songService.getSongsByAlbumId(id);
        int userId = resolveUserId(userDetails);
        markFavorites(songs, userId);
        boolean isArtist = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ARTIST"));
        model.addAttribute("album", album);
        model.addAttribute("songs", songs);
        model.addAttribute("isArtist", isArtist);
        model.addAttribute("title", "RevPlay - " + album.getTitle());
        model.addAttribute("email", userDetails.getUsername());
        return "listener/view_songs";
    }

    @GetMapping("/artists/{id}/songs")
    public String viewArtistSongs(@PathVariable int id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        ArtistAccountResponseDto artist = artistService.getArtistById(id);
        List<SongResponseDto> songs = songService.getSongsByArtistId(id);
        int userId = resolveUserId(userDetails);
        markFavorites(songs, userId);
        boolean isArtist = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ARTIST"));
        model.addAttribute("artist", artist);
        model.addAttribute("songs", songs);
        model.addAttribute("isArtist", isArtist);
        model.addAttribute("title", "RevPlay - " + artist.getStageName());
        model.addAttribute("email", userDetails.getUsername());
        return "listener/view_songs";
    }

    @GetMapping("/genres/{id}/songs")
    public String viewGenreSongs(@PathVariable int id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        GenreResponseDto genre = genreService.getGenreById(id);
        List<SongResponseDto> songs = songService.getSongsByGenreId(id);
        int userId = resolveUserId(userDetails);
        markFavorites(songs, userId);
        boolean isArtist = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ARTIST"));
        model.addAttribute("genre", genre);
        model.addAttribute("songs", songs);
        model.addAttribute("isArtist", isArtist);
        model.addAttribute("title", "RevPlay - " + genre.getGenreName());
        model.addAttribute("email", userDetails.getUsername());
        return "listener/view_songs";
    }
}
