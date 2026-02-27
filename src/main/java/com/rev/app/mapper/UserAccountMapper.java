package com.rev.app.mapper;

import com.rev.app.dto.UserAccountRequestDto;
import com.rev.app.dto.UserAccountResponseDto;
import com.rev.app.entity.UserAccount;
import org.springframework.stereotype.Component;

@Component
public class UserAccountMapper {

    public UserAccount toEntity(UserAccountRequestDto dto) {
        if (dto == null)
            return null;
        UserAccount entity = new UserAccount();
        entity.setFullName(dto.getFullName());
        entity.setEmail(dto.getEmail());
        entity.setPasswordHash(dto.getPassword()); // In a real app, hash this!
        entity.setPhone(dto.getPhone());
        entity.setSecurityQuestion(dto.getSecurityQuestion());
        entity.setSecurityAnswerHash(dto.getSecurityAnswer());
        entity.setPasswordHint(dto.getPasswordHint());
        entity.setStatus("ACTIVE"); // Default
        entity.setProfileImageUrl(dto.getProfileImageUrl());
        return entity;
    }

    public UserAccountResponseDto toResponseDto(UserAccount entity) {
        if (entity == null)
            return null;
        return UserAccountResponseDto.builder()
                .userId(entity.getUserId())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .status(entity.getStatus())
                .profileImageUrl(entity.getProfileImageUrl())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
