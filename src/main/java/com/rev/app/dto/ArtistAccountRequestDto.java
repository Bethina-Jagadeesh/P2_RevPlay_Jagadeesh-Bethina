package com.rev.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistAccountRequestDto {
    private String stageName;
    private String email;
    private String password;
    private String bio;
    private String genre;
    private String instagramLink;
    private String youtubeLink;
    private String spotifyLink;
    private String securityQuestion;
    private String securityAnswer;
}
