package com.jwtproject.service;

import com.jwtproject.domain.User;
import com.jwtproject.dto.UserDetail;
import com.jwtproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("없는 User입니다."));
        return new UserDetail(user);
    }

    // 해당하는 user가 존재한다면 UserDetails 객채로 만들어서 return.
    private UserDetails createUserDetails(UserDetail userDetail) {
        return new org.springframework.security.core.userdetails.User(userDetail.getUsername(), userDetail.getPassword(), userDetail.getAuthorities());
    }
}
