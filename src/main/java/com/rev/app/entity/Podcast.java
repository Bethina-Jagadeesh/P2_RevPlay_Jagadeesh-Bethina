package com.rev.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "podcast")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Podcast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "podcast_id")
    private int podcastId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "host_name")
    private String hostName;

    @Column(name = "category")
    private String category;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "artist_id", nullable = false)
    private int artistId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
