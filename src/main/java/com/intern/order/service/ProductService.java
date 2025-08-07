package com.intern.order.service;

import com.intern.order.dto.CreateProductRequest;
import com.intern.order.dto.ProductResponse;
import com.intern.order.dto.UpdateProductRequest;
import com.intern.order.entity.Product;
import com.intern.order.exception.ProductNotFoundException;
import com.intern.order.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::mapToProductResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return mapToProductResponse(product);
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        log.info("Creating a new product with name: {}", request.getName());
        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .stock(request.getStock())
                .category(request.getCategory())
                .isActive(request.getIsActive())
                .build();
        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with ID: {}", savedProduct.getId());
        return mapToProductResponse(savedProduct);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        log.info("Updating product with ID: {}", id);
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        existingProduct.setName(request.getName());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setStock(request.getStock());
        existingProduct.setCategory(request.getCategory());
        existingProduct.setIsActive(request.getIsActive());

        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Product with ID: {} updated successfully.", id);
        return mapToProductResponse(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        log.warn("Attempting to delete product with ID: {}", id);
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
        log.warn("Product with ID: {} has been deleted.", id);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String name, String category) {
        String searchName = (name == null) ? "" : name;
        String searchCategory = (category == null) ? "" : category;

        return productRepository
                .findByNameContainingIgnoreCaseAndCategoryContainingIgnoreCase(searchName, searchCategory)
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(product.getCategory())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt())
                .build();
    }
}
