package com.rev.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "favorite_song")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(FavoriteSong.FavoriteSongId.class)
public class FavoriteSong {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FavoriteSongId implements Serializable {
        private int userId;
        private int songId;
    }

    @Id
    @Column(name = "user_id")
    private int userId;

    @Id
    @Column(name = "song_id")
    private int songId;

    @Column(name = "favorited_at")
    private LocalDateTime favoritedAt;
}
