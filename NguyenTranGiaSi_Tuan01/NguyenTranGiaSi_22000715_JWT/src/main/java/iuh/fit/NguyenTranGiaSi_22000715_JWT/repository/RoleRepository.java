package iuh.fit.NguyenTranGiaSi_22000715_JWT.repository;

import iuh.fit.NguyenTranGiaSi_22000715_JWT.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
