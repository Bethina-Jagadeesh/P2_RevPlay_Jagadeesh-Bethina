package com.rev.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccountResponseDto {
    private int userId;
    private String fullName;
    private String email;
    private String phone;
    private String status;
    private LocalDateTime createdAt;
}
