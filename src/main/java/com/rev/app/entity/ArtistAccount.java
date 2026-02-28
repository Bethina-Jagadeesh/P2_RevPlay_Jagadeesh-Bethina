package com.rev.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "artist_account")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_id")
    private int artistId;

    @Column(name = "stage_name", nullable = false)
    private String stageName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "genre")
    private String genre;

    @Column(name = "instagram_link")
    private String instagramLink;

    @Column(name = "youtube_link")
    private String youtubeLink;

    @Column(name = "spotify_link")
    private String spotifyLink;

    @Column(name = "security_question")
    private String securityQuestion;

    @Column(name = "security_answer_hash")
    private String securityAnswerHash;

    @Column(name = "password_hint")
    private String passwordHint;

    @Column(name = "status")
    private String status;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
