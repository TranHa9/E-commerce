package vn.techmaster.tranha.ecommerce.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import vn.techmaster.tranha.ecommerce.dto.SearchCategroryDto;
import vn.techmaster.tranha.ecommerce.entity.Category;
import vn.techmaster.tranha.ecommerce.exception.ObjectNotFoundException;
import vn.techmaster.tranha.ecommerce.model.request.CategorySearchRequest;
import vn.techmaster.tranha.ecommerce.model.request.CreateCategoryRequest;
import vn.techmaster.tranha.ecommerce.model.request.UpdateCategoryRequest;
import vn.techmaster.tranha.ecommerce.model.response.*;
import vn.techmaster.tranha.ecommerce.repository.CategoryRepository;
import vn.techmaster.tranha.ecommerce.repository.custom.CategoryCustomRepository;
import vn.techmaster.tranha.ecommerce.statics.CategoryStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {

    CategoryRepository categoryRepository;

    CategoryCustomRepository categoryCustomRepository;

    ObjectMapper objectMapper;

    public CategoryResponse createCategory(CreateCategoryRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .status(CategoryStatus.ACTIVE)
                .build();
        categoryRepository.save(category);
        return objectMapper.convertValue(category, CategoryResponse.class);
    }

    public CommonSearchResponse<?> searchCategory(CategorySearchRequest request) {
        List<SearchCategroryDto> result = categoryCustomRepository.searchCategory(request);

        Long totalRecord = 0L;
        List<CategorySearchResponse> categoryResponses = new ArrayList<>();
        if (!result.isEmpty()) {
            totalRecord = result.get(0).getTotalRecord();
            categoryResponses = result
                    .stream()
                    .map(s -> objectMapper.convertValue(s, CategorySearchResponse.class))
                    .toList();
        }

        int totalPage = (int) Math.ceil((double) totalRecord / request.getPageSize());

        return CommonSearchResponse.<CategorySearchResponse>builder()
                .totalRecord(totalRecord)
                .totalPage(totalPage)
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

    public void updateCategoryStatus(Long id, CategoryStatus status) throws Exception {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isEmpty()) {
            throw new Exception("Category not found");
        }
        Category category = categoryOptional.get();

        category.setStatus(status);
        categoryRepository.save(category);
    }

    public List<CategoryResponse> getAllActiveCategories() {
        return categoryRepository.findByStatus(CategoryStatus.ACTIVE).stream()
                .map(category -> objectMapper.convertValue(category, CategoryResponse.class))
                .toList();
    }
}
