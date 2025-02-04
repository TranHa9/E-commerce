package vn.techmaster.tranha.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.techmaster.tranha.ecommerce.entity.UserAddress;

import java.util.List;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    @Query("SELECT u FROM UserAddress u WHERE u.user.id = :userId")
    List<UserAddress> findByUserId(Long userId);
}
