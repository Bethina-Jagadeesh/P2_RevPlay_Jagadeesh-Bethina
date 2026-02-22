package com.rev.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "playlist_song")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(PlaylistSong.PlaylistSongId.class)
public class PlaylistSong {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlaylistSongId implements Serializable {
        private int playlistId;
        private int songId;
    }

    @Id
    @Column(name = "playlist_id")
    private int playlistId;

    @Id
    @Column(name = "song_id")
    private int songId;

    @Column(name = "added_at")
    private LocalDateTime addedAt;
}
