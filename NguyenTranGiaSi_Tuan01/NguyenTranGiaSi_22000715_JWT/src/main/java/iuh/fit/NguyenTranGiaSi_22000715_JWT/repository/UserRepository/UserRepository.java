package iuh.fit.NguyenTranGiaSi_22000715_JWT.repository.UserRepository;

import iuh.fit.NguyenTranGiaSi_22000715_JWT.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
