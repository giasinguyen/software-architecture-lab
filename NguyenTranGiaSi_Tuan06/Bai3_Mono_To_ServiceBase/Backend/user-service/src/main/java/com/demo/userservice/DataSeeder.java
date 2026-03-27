package com.demo.userservice;

import com.demo.userservice.entity.User;
import com.demo.userservice.enums.UserRole;
import com.demo.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() == 0) {
            userRepository.saveAll(List.of(
                    User.builder().name("Nguyễn Văn An").email("user@gmail.com")
                            .phone("0901234567").address("123 Lê Lợi, Q1, TP.HCM").role(UserRole.USER).build(),
                    User.builder().name("Admin Restaurant").email("admin@gmail.com")
                            .phone("0987654321").address("456 Nguyễn Huệ, Q1, TP.HCM").role(UserRole.ADMIN).build()
            ));
        }
    }
}
