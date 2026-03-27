package com.demo.vertical.controller;

import com.demo.vertical.service.VerticalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/vertical")
@RequiredArgsConstructor
public class VerticalController {

    private final VerticalService verticalService;

    // GET /api/vertical/users/full  – bảng chưa tách (8 cột)
    @GetMapping("/users/full")
    public ResponseEntity<?> getFull() {
        return ResponseEntity.ok(verticalService.getAllFull());
    }

    // GET /api/vertical/users/basic  – vertical partition: 4 cột hot
    @GetMapping("/users/basic")
    public ResponseEntity<?> getBasic() {
        return ResponseEntity.ok(verticalService.getAllBasic());
    }

    // GET /api/vertical/users/profile  – vertical partition: avatar, bio
    @GetMapping("/users/profile")
    public ResponseEntity<?> getProfile() {
        return ResponseEntity.ok(verticalService.getAllProfile());
    }

    // GET /api/vertical/users/activity  – vertical partition: last_login, settings
    @GetMapping("/users/activity")
    public ResponseEntity<?> getActivity() {
        return ResponseEntity.ok(verticalService.getAllActivity());
    }

    // GET /api/vertical/benchmark
    @GetMapping("/benchmark")
    public Map<String, Object> benchmark() {
        return verticalService.benchmark();
    }
}
