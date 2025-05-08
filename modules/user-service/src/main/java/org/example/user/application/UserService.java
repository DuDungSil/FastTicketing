package org.example.user.application;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.example.common.utils.RandomNameGenUtil;
import org.example.user.adapter.out.repository.UserRepository;
import org.example.user.domain.entity.User;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RandomNameGenUtil randomNameGenUtill;

    // 더미 유저 생성
    @Transactional
    public void generateDummyUsers(int count) {
        List<User> users = IntStream.rangeClosed(1, count)
                .mapToObj(i -> new User(randomNameGenUtill.generateRandomKoreanName()))
                .collect(Collectors.toList());

        userRepository.saveAll(users);
    }

}
