package vn.techmaster.tranha.ecommerce.resource;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import vn.techmaster.tranha.ecommerce.exception.ObjectNotFoundException;
import vn.techmaster.tranha.ecommerce.model.request.CategorySearchRequest;
import vn.techmaster.tranha.ecommerce.model.request.CreateCategoryRequest;
import vn.techmaster.tranha.ecommerce.model.request.UpdateCategoryRequest;
import vn.techmaster.tranha.ecommerce.model.response.CategoryResponse;
import vn.techmaster.tranha.ecommerce.model.response.CommonSearchResponse;
import vn.techmaster.tranha.ecommerce.service.CategoryService;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/categories")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryResource {

    CategoryService categoryService;

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

}
