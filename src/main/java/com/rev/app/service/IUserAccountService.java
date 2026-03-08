package com.rev.app.service;

import com.rev.app.dto.UserAccountRequestDto;
import com.rev.app.dto.UserAccountResponseDto;
import java.util.List;

public interface IUserAccountService {
    UserAccountResponseDto createUser(UserAccountRequestDto requestDto);

    UserAccountResponseDto getUserById(int id);

    UserAccountResponseDto getUserByEmail(String email);

    List<UserAccountResponseDto> getAllUsers();

    UserAccountResponseDto updateUser(int id, UserAccountRequestDto requestDto);

    void deleteUser(int id);
}
