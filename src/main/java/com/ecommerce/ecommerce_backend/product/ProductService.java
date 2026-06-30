package com.ecommerce.ecommerce_backend.product;

import com.ecommerce.ecommerce_backend.category.Category;
import com.ecommerce.ecommerce_backend.category.CategoryRepository;
import com.ecommerce.ecommerce_backend.common.util.StringUtils;
import com.ecommerce.ecommerce_backend.product.dto.ProductMapper;
import com.ecommerce.ecommerce_backend.product.dto.ProductRequest;
import com.ecommerce.ecommerce_backend.product.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating product with name={}, price={}, quantity={}",
                request.getName(), request.getPrice(), request.getStockQuantity());
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new RuntimeException("Category not found with id: " + request.getCategoryId()));

        String normalizedName = StringUtils.normalize(request.getName());

        if(productRepository.existsByNameIgnoreCaseAndCategoryId(
                normalizedName, request.getCategoryId())) {
            log.warn("Product with name={} already exists in this category",
                    normalizedName);
            throw new RuntimeException("Product already exists in this category: " + normalizedName);
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
                        new RuntimeException("Product not found with id: " + id)
                );

        return productMapper.toResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        if(!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException("Category not found with id: " + categoryId);
        }
        return productRepository.findByCategoryId(categoryId)
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.info("Updating Product with id={}",  id);
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found with id: " + id)
                );

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new RuntimeException("Category not found with id: " + request.getCategoryId())
                );

        String normalizedName = StringUtils.normalize(request.getName());

        if(productRepository.existsByNameIgnoreCaseAndCategoryIdAndIdNot(
                normalizedName, request.getCategoryId(), id)) {
            log.warn("Another Product already has this name={}", normalizedName);
            throw new RuntimeException("Another Product already has this name in this category: " + normalizedName);
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
                        new RuntimeException("Product not found with id: " + id)
                );

        productRepository.delete(product);
        log.info("Product deleted successfully - with id={}", id);
    }


}
