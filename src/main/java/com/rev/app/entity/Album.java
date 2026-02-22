package com.rev.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "album")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "album_id")
    private int albumId;

    @Column(name = "artist_id", nullable = false)
    private int artistId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
