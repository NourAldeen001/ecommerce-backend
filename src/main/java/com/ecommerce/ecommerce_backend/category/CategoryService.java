package com.ecommerce.ecommerce_backend.category;

import com.ecommerce.ecommerce_backend.category.dto.CategoryMapper;
import com.ecommerce.ecommerce_backend.category.dto.CategoryRequest;
import com.ecommerce.ecommerce_backend.category.dto.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.ecommerce.ecommerce_backend.common.util.StringUtils.*;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {

        String normalizedName = normalize(request.getName());

        if(categoryRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new RuntimeException(
                    "Category already exists: " + normalizedName);
        }

        Category category = categoryMapper.toEntity(request);

        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(savedCategory);
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                    new RuntimeException("Category not found with id: " + id)
                );
        return categoryMapper.toResponse(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Category not found with id: " + id)
                );

        String normalizedName = normalize(request.getName());

        if(categoryRepository.existsByNameIgnoreCaseAndIdNot(normalizedName, id)) {
            throw new RuntimeException("Category name already taken: " + normalizedName);
        }

        category.setName(normalizedName);
        category.setDescription(request.getDescription());

        Category updatedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Category not found with id: " + id)
                );

        if(!category.getProducts().isEmpty()) {
            throw new RuntimeException("Cannot delete category with existing products. " +
                    "Reassign or delete products first. ");
        }

        categoryRepository.delete(category);
    }

}
