package com.rushcrew.user_service.user.infrastructure.validator;

import com.rushcrew.user_service.user.domain.entity.User;
import com.rushcrew.user_service.user.domain.repository.UserRepository;
import com.rushcrew.user_service.user.domain.service.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidatorImpl implements UserValidator {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // TODO 글로벌 예외 처리 적용 후 커스텀 예외로 변경
    @Override
    public void validateEmailUniqueness(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
    }

    @Override
    public void validatePassword(User user, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }
}
