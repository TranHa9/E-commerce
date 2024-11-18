package vn.techmaster.tranha.ecommerce.repository;


import vn.techmaster.tranha.ecommerce.entity.User;
import vn.techmaster.tranha.ecommerce.statics.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndStatus(String username, UserStatus status);

}