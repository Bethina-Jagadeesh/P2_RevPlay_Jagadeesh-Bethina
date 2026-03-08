package com.rev.app.service;

import com.rev.app.dto.UserAccountRequestDto;
import com.rev.app.dto.UserAccountResponseDto;
import com.rev.app.entity.UserAccount;
import com.rev.app.mapper.UserAccountMapper;
import com.rev.app.repository.IUserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserAccountServiceImpl implements IUserAccountService {

    private static final Logger logger = LoggerFactory.getLogger(UserAccountServiceImpl.class);

    private final IUserAccountRepository userRepository;
    private final UserAccountMapper userAccountMapper;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public UserAccountServiceImpl(IUserAccountRepository userRepository,
            UserAccountMapper userAccountMapper,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userAccountMapper = userAccountMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserAccountResponseDto createUser(UserAccountRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            logger.warn("Creation failed: User with email {} already exists.", requestDto.getEmail());
            throw new RuntimeException("Email already exists");
        }
        UserAccount user = userAccountMapper.toEntity(requestDto);
        user.setPasswordHash(passwordEncoder.encode(requestDto.getPassword()));
        user.setCreatedAt(java.time.LocalDateTime.now());
        if (requestDto.getSecurityAnswer() != null && !requestDto.getSecurityAnswer().isBlank()) {
            user.setSecurityQuestion(requestDto.getSecurityQuestion());
            user.setSecurityAnswerHash(passwordEncoder.encode(requestDto.getSecurityAnswer()));
        }
        user = userRepository.save(user);
        logger.info("Successfully created listener account for email: {}", user.getEmail());
        return userAccountMapper.toResponseDto(user);
    }

    @Override
    public UserAccountResponseDto getUserById(int id) {
        return userRepository.findById(id)
                .map(userAccountMapper::toResponseDto)
                .orElse(null);
    }

    @Override
    public UserAccountResponseDto getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userAccountMapper::toResponseDto)
                .orElse(null);
    }

    @Override
    public List<UserAccountResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userAccountMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserAccountResponseDto updateUser(int id, UserAccountRequestDto requestDto) {
        UserAccount existingUser = userRepository.findById(id).orElse(null);
        if (existingUser != null) {
            existingUser.setFullName(requestDto.getFullName());
            existingUser.setPhone(requestDto.getPhone());
            userRepository.save(existingUser);
            return userAccountMapper.toResponseDto(existingUser);
        }
        return null;
    }

    @Override
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }
}
