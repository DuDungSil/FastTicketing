package org.example.user.adapter.in;

import org.example.user.application.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 더미 유저 데이터 생성
    @PostMapping("/dummy")
    public ResponseEntity<String> generateDummyUsers(@RequestParam("count") int count) {
        userService.generateDummyUsers(count);
        return ResponseEntity.ok(count + " dummy users created.");
    }

}
