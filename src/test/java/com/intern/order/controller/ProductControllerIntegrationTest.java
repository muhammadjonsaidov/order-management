package com.intern.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intern.order.dto.CreateProductRequest;
import com.intern.order.dto.UpdateProductRequest;
import com.intern.order.entity.Product;
import com.intern.order.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional // Bu annotatsiya har bir testdan keyin ma'lumotlar bazasini tozalaydi
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("Create Product: Should create a new product and return 201 Created")
    void createProduct_withValidData_shouldReturn201Created() throws Exception {
        // Arrange
        CreateProductRequest request = new CreateProductRequest();
        request.setName("New Awesome Laptop");
        request.setPrice(new BigDecimal("1500.99"));
        request.setStock(50);
        request.setCategory("Electronics");
        request.setIsActive(true);

        // Act
        ResultActions result = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Assert
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name", is("New Awesome Laptop")))
                .andExpect(jsonPath("$.price", is(1500.99)))
                .andExpect(jsonPath("$.stock", is(50)));
    }

    @Test
    @DisplayName("Get Product By ID: Should return product when ID exists")
    void getProductById_whenProductExists_shouldReturn200OK() throws Exception {
        // Arrange: Test uchun ma'lumotni shu testning o'zida yaratamiz
        Product product = productRepository.save(Product.builder()
                .name("Test Camera")
                .price(new BigDecimal("750.00"))
                .stock(20)
                .category("Photography")
                .isActive(true)
                .build());

        // Act
        ResultActions result = mockMvc.perform(get("/api/products/" + product.getId()));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(product.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Test Camera")));
    }

    @Test
    @DisplayName("Get Product By ID: Should return 404 Not Found when ID does not exist")
    void getProductById_whenProductDoesNotExist_shouldReturn404NotFound() throws Exception {
        // Arrange
        long nonExistentId = 999L;

        // Act
        ResultActions result = mockMvc.perform(get("/api/products/" + nonExistentId));

        // Assert
        result.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete Product: Should delete product and return 204 No Content")
    void deleteProduct_whenProductExists_shouldReturn204NoContent() throws Exception {
        // Arrange
        Product product = productRepository.save(Product.builder()
                .name("Product to be deleted")
                .price(new BigDecimal("10.00"))
                .stock(5)
                .isActive(true)
                .build());

        // Act
        ResultActions result = mockMvc.perform(delete("/api/products/" + product.getId()));

        // Assert
        result.andExpect(status().isNoContent());

        // Bazadan haqiqatdan ham o'chirilganini tekshiramiz
        assertFalse(productRepository.findById(product.getId()).isPresent());
    }

    @Test
    void getAllProducts_withPagination_shouldReturnPagedResults() throws Exception {
        // Arrange
        productRepository.save(Product.builder().name("Product 4").price(BigDecimal.TEN).stock(1).isActive(true).build());
        productRepository.save(Product.builder().name("Product 5").price(BigDecimal.TEN).stock(1).isActive(true).build());

        // Act & Assert
        mockMvc.perform(get("/api/products?page=0&size=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalPages", is(5)))
                .andExpect(jsonPath("$.totalElements", is(5)));
    }

    @Test
    void updateProduct_whenProductExists_shouldReturn200OK() throws Exception {
        // Arrange
        Product product = productRepository.save(Product.builder().name("Old Name").price(BigDecimal.TEN).stock(10).isActive(true).build());
        UpdateProductRequest updateRequest = new UpdateProductRequest();
        updateRequest.setName("New Updated Name");
        updateRequest.setPrice(new BigDecimal("15.50"));
        updateRequest.setStock(20);
        updateRequest.setIsActive(false);

        // Act & Assert
        mockMvc.perform(put("/api/products/" + product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("New Updated Name")))
                .andExpect(jsonPath("$.isActive", is(false)));
    }

    @Test
    void searchProducts_shouldReturnMatchingProducts() throws Exception {
        // Arrange
        productRepository.save(Product.builder().name("Apple iPhone 15").price(BigDecimal.TEN).stock(1).category("Mobile").isActive(true).build());
        productRepository.save(Product.builder().name("Apple MacBook Pro").price(BigDecimal.TEN).stock(1).category("Laptop").isActive(true).build());

        // Act & Assert
        mockMvc.perform(get("/api/products/search?name=Apple&category=Mobile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Apple iPhone 15")));
    }

    @Test
    @DisplayName("Create Product: Should return 400 Bad Request for invalid data")
    void createProduct_withInvalidData_shouldReturn400BadRequest() throws Exception {
        // Arrange
        // Nomini bo'sh qoldiramiz, bu @NotBlank validatsiyasini buzadi
        CreateProductRequest request = new CreateProductRequest();
        request.setName(""); // Invalid name
        request.setPrice(new BigDecimal("1500.99"));
        request.setStock(50);
        request.setCategory("Electronics");
        request.setIsActive(true);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Product name must be between 2 and 100 characters")); // GlobalExceptionHandler qaytargan xabar
    }

    @Test
    @DisplayName("Update Product: Should return 404 Not Found for non-existent ID")
    void updateProduct_whenProductDoesNotExist_shouldReturn404NotFound() throws Exception {
        // Arrange
        UpdateProductRequest request = new UpdateProductRequest();
        request.setName("Does not matter");
        request.setPrice(new BigDecimal("10.00"));
        request.setStock(1);
        request.setIsActive(true);

        // Act & Assert
        mockMvc.perform(put("/api/products/999") // Mavjud bo'lmagan ID
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Product not found with id: 999")); // GlobalExceptionHandler qaytargan xabar
    }
}