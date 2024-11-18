package vn.techmaster.tranha.ecommerce.repository;

import vn.techmaster.tranha.ecommerce.entity.Role;
import vn.techmaster.tranha.ecommerce.statics.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(Roles name);

}
