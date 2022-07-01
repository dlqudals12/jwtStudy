package com.jwtproject.service;

import com.jwtproject.domain.Authority;
import com.jwtproject.domain.User;
import com.jwtproject.dto.Response;
import com.jwtproject.dto.UserRequestDto;
import com.jwtproject.jwt.JwtTokenProvider;
import com.jwtproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@Service
public class UsersService {

    private final UserRepository userRepository;
    private final Response response;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;


    public ResponseEntity<?> signUp(UserRequestDto.SignUp signUp) {
        if (userRepository.existsByEmail(signUp.getEmail())) {
            return response.fail("이미 회원가입된 이메일입니다.", HttpStatus.BAD_REQUEST);
        }

        User user = User.builder()
                .email(signUp.getEmail())
                .password(passwordEncoder.encode(signUp.getPassword()))
                .roles(Collections.singletonList(Authority.ROLE_USER.name()))
                .build();

        userRepository.save(user);

        return response.success("회원가입에 성공했습니다.");

    }


}
