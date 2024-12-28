package vn.techmaster.tranha.ecommerce.resource;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.techmaster.tranha.ecommerce.exception.ObjectNotFoundException;
import vn.techmaster.tranha.ecommerce.model.request.CategorySearchRequest;
import vn.techmaster.tranha.ecommerce.model.request.CreateCategoryRequest;
import vn.techmaster.tranha.ecommerce.model.request.UpdateCategoryRequest;
import vn.techmaster.tranha.ecommerce.model.response.CategoryResponse;
import vn.techmaster.tranha.ecommerce.model.response.CommonSearchResponse;
import vn.techmaster.tranha.ecommerce.service.CategoryService;
import vn.techmaster.tranha.ecommerce.statics.CategoryStatus;
import vn.techmaster.tranha.ecommerce.statics.ProductStatus;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryResource {

    CategoryService categoryService;

    @GetMapping("/active")
    public List<CategoryResponse> getAllActiveCategories() {
        return categoryService.getAllActiveCategories();
    }

    @PostMapping
    public CategoryResponse create(@RequestBody @Valid CreateCategoryRequest request) {
        return categoryService.createCategory(request);
    }

    @GetMapping
    public CommonSearchResponse<?> search(CategorySearchRequest request) {
        return categoryService.searchCategory(request);
    }

    @GetMapping("/{id}")
    public CategoryResponse getDetail(@PathVariable Long id) throws ObjectNotFoundException {
        return categoryService.getDetail(id);
    }

    @PutMapping("/{id}")
    public CategoryResponse updateUser(@PathVariable Long id, @RequestBody @Valid UpdateCategoryRequest request) throws ObjectNotFoundException, IOException {
        return categoryService.updateCategory(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) throws ObjectNotFoundException {
        categoryService.deleteCategory(id);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateCategoryStatus(@PathVariable Long id, @RequestBody CategoryStatus status) {
        try {
            categoryService.updateCategoryStatus(id, status);
            return ResponseEntity.ok("Product status updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
