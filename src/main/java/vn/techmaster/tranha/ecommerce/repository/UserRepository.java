package vn.techmaster.tranha.ecommerce.repository;


import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.techmaster.tranha.ecommerce.entity.User;
import vn.techmaster.tranha.ecommerce.statics.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u join fetch u.roles where u.id = :id")
    Optional<User> findUserWithRolesById(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndStatus(String email, UserStatus status);

}