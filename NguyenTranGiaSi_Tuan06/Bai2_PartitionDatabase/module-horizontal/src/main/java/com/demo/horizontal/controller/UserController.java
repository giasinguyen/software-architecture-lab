package com.demo.horizontal.controller;

import com.demo.horizontal.dto.UserRequest;
import com.demo.horizontal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/horizontal")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // POST /api/horizontal/users  body: { "name":"...", "email":"...", "gender":"M" }
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody UserRequest req) {
        return ResponseEntity.ok(userService.save(req));
    }

    // GET /api/horizontal/users/male
    @GetMapping("/users/male")
    public ResponseEntity<?> getMales() {
        return ResponseEntity.ok(userService.getAllMale());
    }

    // GET /api/horizontal/users/female
    @GetMapping("/users/female")
    public ResponseEntity<?> getFemales() {
        return ResponseEntity.ok(userService.getAllFemale());
    }

    // GET /api/horizontal/benchmark
    @GetMapping("/benchmark")
    public Map<String, Object> benchmark() {
        return userService.benchmark();
    }
}
