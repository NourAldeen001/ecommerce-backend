package com.ecommerce.ecommerce_backend.category;

import com.ecommerce.ecommerce_backend.category.dto.CategoryMapper;
import com.ecommerce.ecommerce_backend.category.dto.CategoryRequest;
import com.ecommerce.ecommerce_backend.category.dto.CategoryResponse;
import com.ecommerce.ecommerce_backend.common.exception.BusinessRuleViolationException;
import com.ecommerce.ecommerce_backend.common.exception.DuplicateResourceException;
import com.ecommerce.ecommerce_backend.common.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce_backend.common.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import static com.ecommerce.ecommerce_backend.common.util.PaginationValidator.*;

import static com.ecommerce.ecommerce_backend.common.util.StringUtils.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    private static final Set<String> SORTABLE_FIELDS = Set.of("name");

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        log.info("Creating a category with name={}", request.getName());
        String normalizedName = normalize(request.getName());

        if(categoryRepository.existsByNameIgnoreCase(normalizedName)) {
            log.warn("Creating Category rejected -" +
                    " this category already exists with name={}", normalizedName);
            throw new DuplicateResourceException(
                    "Category already exists: " + normalizedName);
        }

        Category category = categoryMapper.toEntity(request);

        Category savedCategory = categoryRepository.save(category);

        log.info("Category created successfully - categoryName={}", normalizedName);

        return categoryMapper.toResponse(savedCategory);
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Category not found with id: " + id)
                );
        return categoryMapper.toResponse(category);
    }

    @Transactional(readOnly = true)
    public PagedResponse<CategoryResponse> getAllCategories(
            int page, int size, String direction) {

        validatePageParams(page, size);
        String validatedSortField = validateSortField(SORTABLE_FIELDS, "name", "name");
        Sort sort = buildSort(validatedSortField, direction);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CategoryResponse> categoryResponsePage = categoryRepository
                .findAll(pageable)
                .map(categoryMapper::toResponse);

        return PagedResponse.of(categoryResponsePage);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        log.info("Updating category with id={}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found with id: " + id)
                );

        String normalizedName = normalize(request.getName());

        if(categoryRepository.existsByNameIgnoreCaseAndIdNot(normalizedName, id)) {
            log.warn("Category name={} already taken", normalizedName);
            throw new DuplicateResourceException("Category name already taken: " + normalizedName);
        }

        category.setName(normalizedName);
        category.setDescription(request.getDescription());

        Category updatedCategory = categoryRepository.save(category);

        log.info("Category updated successfully - category with updatedName={}", normalizedName);

        return categoryMapper.toResponse(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        log.info("Deleting a category with id={}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found with id: " + id)
                );

        if(!category.getProducts().isEmpty()) {
            log.warn("Cannot delete a category with id={}," +
                    " because category has products. ", id);
            throw new BusinessRuleViolationException("Cannot delete category with existing products. " +
                    "Reassign or delete products first. ");
        }

        categoryRepository.delete(category);
    }

}
