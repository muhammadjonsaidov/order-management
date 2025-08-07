package com.intern.order.service;

import com.intern.order.dto.CreateProductRequest;
import com.intern.order.dto.ProductResponse;
import com.intern.order.entity.Product;
import com.intern.order.exception.ProductNotFoundException;
import com.intern.order.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(new BigDecimal("10.00"))
                .stock(100)
                .isActive(true)
                .category("Test Category")
                .build();
    }

    @Test
    void getProductById_whenProductExists_shouldReturnProductResponse() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        ProductResponse response = productService.getProductById(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Test Product");
    }

    @Test
    void getProductById_whenProductDoesNotExist_shouldThrowProductNotFoundException() {
        // Arrange
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductById(99L);
        });
    }
    
    @Test
    void createProduct_shouldReturnSavedProductResponse() {
        // Arrange
        CreateProductRequest request = new CreateProductRequest();
        request.setName("New Product");
        request.setPrice(new BigDecimal("25.00"));
        request.setStock(50);
        request.setIsActive(true);
        
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        ProductResponse response = productService.createProduct(request);
        
        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    @Test
    void deleteProduct_whenProductExists_shouldCallDelete() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L); // deleteById void qaytargani uchun

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void deleteProduct_whenProductDoesNotExist_shouldThrowException() {
        // Arrange
        when(productRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> {
            productService.deleteProduct(99L);
        });
        
        verify(productRepository, never()).deleteById(anyLong());
    }
}
