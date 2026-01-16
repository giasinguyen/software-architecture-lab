package iuh.fit.NguyenTranGiaSi_22000715_JWT.controller;

import iuh.fit.NguyenTranGiaSi_22000715_JWT.dto.LoginRequest;
import iuh.fit.NguyenTranGiaSi_22000715_JWT.entity.User;
import iuh.fit.NguyenTranGiaSi_22000715_JWT.repository.UserRepository.UserRepository;
import iuh.fit.NguyenTranGiaSi_22000715_JWT.service.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepo,
                          PasswordEncoder encoder,
                          JwtService jwtService) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest req) {

        User user = userRepo.findByUsername(req.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return Map.of(
            "accessToken", jwtService.generateAccessToken(user),
            "refreshToken", jwtService.generateRefreshToken(user)
        );
    }
}
