package com.rev.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "podcast_episode")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PodcastEpisode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "episode_id")
    private int episodeId;

    @Column(name = "podcast_id", nullable = false)
    private int podcastId;

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

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
