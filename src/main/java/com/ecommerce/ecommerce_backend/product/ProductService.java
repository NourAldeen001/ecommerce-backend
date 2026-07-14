package com.ecommerce.ecommerce_backend.product;

import com.ecommerce.ecommerce_backend.category.Category;
import com.ecommerce.ecommerce_backend.category.CategoryRepository;
import com.ecommerce.ecommerce_backend.common.exception.DuplicateResourceException;
import com.ecommerce.ecommerce_backend.common.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce_backend.common.response.PagedResponse;
import com.ecommerce.ecommerce_backend.common.util.StringUtils;
import com.ecommerce.ecommerce_backend.product.dto.ProductMapper;
import com.ecommerce.ecommerce_backend.product.dto.ProductRequest;
import com.ecommerce.ecommerce_backend.product.dto.ProductResponse;
import com.ecommerce.ecommerce_backend.product.dto.ProductSearchRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import static com.ecommerce.ecommerce_backend.common.util.PaginationValidator.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    private static final Set<String> SORTABLE_FIELDS = Set.of(
            "createdAt", "name", "stockQuantity", "price");

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating product with name={}, price={}, quantity={}",
                request.getName(), request.getPrice(), request.getStockQuantity());
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        String normalizedName = StringUtils.normalize(request.getName());

        if(productRepository.existsByNameIgnoreCaseAndCategoryId(
                normalizedName, request.getCategoryId())) {
            log.warn("Product with name={} already exists in this category",
                    normalizedName);
            throw new DuplicateResourceException("Product already exists in this category: " + normalizedName);
        }

        Product product = productMapper.toEntity(request);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        log.info("Product created successfully - with name={}", savedProduct.getName());

        return productMapper.toResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found with id: " + id)
                );

        return productMapper.toResponse(product);
    }

    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> searchProducts(
            ProductSearchRequest searchRequest,
            int page, int size, String sortBy, String direction) {

        validatePageParams(page, size);
        String validatedSortField = validateSortField(
                SORTABLE_FIELDS, sortBy, "createdAt");
        Sort sort = buildSort(validatedSortField, direction);
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Product> spec = Specification
                .where(ProductSpecification.nameOrDescriptionContains(searchRequest.getKeyword()))
                .and(ProductSpecification.hasCategory(searchRequest.getCategoryId()))
                .and(ProductSpecification.priceGreaterThanOrEqual(searchRequest.getMinPrice()))
                .and(ProductSpecification.priceLessThanOrEqual(searchRequest.getMaxPrice()));
        if(Boolean.TRUE.equals(searchRequest.getInStockOnly())) {
            spec = spec.and(ProductSpecification.inStock());
        }

        Page<ProductResponse> productResponsePage = productRepository
                .findAll(spec, pageable)
                .map(productMapper::toResponse);

        log.info("Product search - keyword={}, categoryId={}, minPrice={}, maxPrice={}," +
                " inStockOnly={}, results={}", searchRequest.getKeyword(), searchRequest.getCategoryId(),
                searchRequest.getMinPrice(), searchRequest.getMaxPrice(), searchRequest.getInStockOnly(),
                productResponsePage.getTotalElements());

        return PagedResponse.of(productResponsePage);
    }

    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getAllProducts(
            int page, int size, String sortBy, String direction) {

        validatePageParams(page, size);
        String validatedSortField = validateSortField(
                SORTABLE_FIELDS, sortBy, "createdAt");
        Sort sort = buildSort(validatedSortField, direction);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductResponse> productResponsePage = productRepository
                .findAll(pageable)
                .map(productMapper::toResponse);

        return PagedResponse.of(productResponsePage);
    }

    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getProductsByCategory(
            Long categoryId, int page, int size, String sortBy, String direction) {
        if(!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }

        validatePageParams(page, size);
        String validatedSortField = validateSortField(SORTABLE_FIELDS, sortBy, "createdAt");
        Sort sort = buildSort(validatedSortField, direction);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductResponse> productResponsePage = productRepository
                .findByCategoryId(categoryId, pageable)
                .map(productMapper::toResponse);

        return PagedResponse.of(productResponsePage);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.info("Updating Product with id={}",  id);
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found with id: " + id)
                );

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found with id: " + request.getCategoryId())
                );

        String normalizedName = StringUtils.normalize(request.getName());

        if(productRepository.existsByNameIgnoreCaseAndCategoryIdAndIdNot(
                normalizedName, request.getCategoryId(), id)) {
            log.warn("Another Product already has this name={}", normalizedName);
            throw new DuplicateResourceException("Another Product already has this name in this category: " + normalizedName);
        }

        product.setName(normalizedName);
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        log.info("Product updated successfully - with id={}, updatedName={}",
                id, normalizedName);

        return productMapper.toResponse(savedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        log.info("Deleting product with id={}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found with id: " + id)
                );

        productRepository.delete(product);
        log.info("Product deleted successfully - with id={}", id);
    }


}
