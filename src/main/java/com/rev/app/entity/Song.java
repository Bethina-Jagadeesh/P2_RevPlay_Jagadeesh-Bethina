package com.rev.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "song")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "song_id")
    private int songId;

    @Column(name = "artist_id", nullable = false)
    private int artistId;

    @Column(name = "album_id")
    private Integer albumId;

    @Column(name = "genre_id", nullable = false)
    private int genreId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "duration_seconds", nullable = false)
    private int durationSeconds;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "play_count")
    private int playCount;

    @Column(name = "is_active")
    private String isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Transient
    private String genreName;
}
