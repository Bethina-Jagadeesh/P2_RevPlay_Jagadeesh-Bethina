package com.rev.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPlayCountDto {
    private int userId;
    private String fullName;
    private String email;
    private String profileImageUrl;
    private long playCount;
}
