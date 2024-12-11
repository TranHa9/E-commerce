package vn.techmaster.tranha.ecommerce.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.techmaster.tranha.ecommerce.entity.Category;
import vn.techmaster.tranha.ecommerce.exception.ObjectNotFoundException;
import vn.techmaster.tranha.ecommerce.model.request.CategorySearchRequest;
import vn.techmaster.tranha.ecommerce.model.request.CreateCategoryRequest;
import vn.techmaster.tranha.ecommerce.model.request.UpdateCategoryRequest;
import vn.techmaster.tranha.ecommerce.model.response.*;
import vn.techmaster.tranha.ecommerce.repository.CategoryRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {

    CategoryRepository categoryRepository;

    ObjectMapper objectMapper;

    public CategoryResponse createCategory(CreateCategoryRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        categoryRepository.save(category);
        return objectMapper.convertValue(category, CategoryResponse.class);
    }

    public CommonSearchResponse<?> searchCategory(CategorySearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPageIndex(), request.getPageSize());
        Page<Category> result = categoryRepository.findCategoriesByName(request.getName(), pageable);

        List<CategorySearchResponse> categoryResponses = result.getContent()
                .stream()
                .map(s -> objectMapper.convertValue(s, CategorySearchResponse.class))
                .toList();

        return CommonSearchResponse.<CategorySearchResponse>builder()
                .totalRecord(result.getTotalElements())
                .totalPage(result.getTotalPages())
                .data(categoryResponses)
                .pageInfo(new CommonSearchResponse.CommonPagingResponse(request.getPageSize(), request.getPageIndex()))
                .build();
    }

    public CategoryResponse getDetail(Long id) throws ObjectNotFoundException {
        return categoryRepository.findById(id)
                .map(category -> objectMapper.convertValue(category, CategoryResponse.class))
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));
    }

    public CategoryResponse updateCategory(Long id, UpdateCategoryRequest request) throws ObjectNotFoundException {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        categoryRepository.save(category);
        return objectMapper.convertValue(category, CategoryResponse.class);
    }

    public void deleteCategory(Long id) throws ObjectNotFoundException {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Category not found"));
        categoryRepository.delete(category);
    }
}
