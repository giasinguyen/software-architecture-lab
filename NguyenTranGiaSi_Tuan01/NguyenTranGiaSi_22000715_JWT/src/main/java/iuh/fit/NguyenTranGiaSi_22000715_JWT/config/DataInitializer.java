package iuh.fit.NguyenTranGiaSi_22000715_JWT.config;

import iuh.fit.NguyenTranGiaSi_22000715_JWT.entity.Role;
import iuh.fit.NguyenTranGiaSi_22000715_JWT.entity.User;
import iuh.fit.NguyenTranGiaSi_22000715_JWT.repository.RoleRepository;
import iuh.fit.NguyenTranGiaSi_22000715_JWT.repository.UserRepository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, 
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        if (userRepository.count() == 0) {
            // Create and save roles first
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            adminRole = roleRepository.save(adminRole);

            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            userRole = roleRepository.save(userRole);

            // Create admin user
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of(adminRole));
            userRepository.save(admin);

            // Create normal user
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRoles(Set.of(userRole));
            userRepository.save(user);

            System.out.println("✅ Test data initialized!");
            System.out.println("Admin: username=admin, password=admin123");
            System.out.println("User: username=user, password=user123");
        }
    }
}
