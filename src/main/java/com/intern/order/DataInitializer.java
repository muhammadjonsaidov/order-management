package com.intern.order;

import com.intern.order.entity.User;
import com.intern.order.enums.Role;
import com.intern.order.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // ADMIN foydalanuvchisini yaratish (agar mavjud bo'lmasa)
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ROLE_ADMIN)
                    .build();
            userRepository.save(admin);
        }

        // USER foydalanuvchisini yaratish (agar mavjud bo'lmasa)
        if (userRepository.findByUsername("user").isEmpty()) {
            User regularUser = User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("user123"))
                    .role(Role.ROLE_USER)
                    .build();
            userRepository.save(regularUser);
        }
    }
}
