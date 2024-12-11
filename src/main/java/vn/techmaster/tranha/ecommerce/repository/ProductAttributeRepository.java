package vn.techmaster.tranha.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.techmaster.tranha.ecommerce.entity.ProductAttribute;

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {
    boolean existsByName(String name);
}
