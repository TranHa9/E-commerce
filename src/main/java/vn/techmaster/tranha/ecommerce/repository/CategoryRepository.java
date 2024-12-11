package vn.techmaster.tranha.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.techmaster.tranha.ecommerce.entity.Category;


public interface CategoryRepository extends JpaRepository<Category, Long> {


    @Query("select c from Category c where (:name is null or c.name like %:name%)")
    Page<Category> findCategoriesByName(String name, Pageable pageable);
}
