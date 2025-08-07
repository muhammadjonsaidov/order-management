package com.intern.order.service;

import com.intern.order.dto.CreateOrderRequest;
import com.intern.order.dto.OrderItemRequest;
import com.intern.order.dto.OrderResponse;
import com.intern.order.entity.Order;
import com.intern.order.entity.Product;
import com.intern.order.exception.InsufficientStockException;
import com.intern.order.exception.ProductNotFoundException;
import com.intern.order.repository.OrderRepository;
import com.intern.order.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito'ni ishga tushirish uchun
class OrderServiceTest {

    @Mock // Soxta (mock) obyekt yaratamiz
    private ProductRepository productRepository;

    @Mock // Soxta (mock) obyekt yaratamiz
    private OrderRepository orderRepository;

    @InjectMocks // Yuqoridagi mock'larni bu obyektga inject qilamiz
    private OrderService orderService;

    private Product product;
    private CreateOrderRequest createOrderRequest;

    @BeforeEach
    void setUp() {
        // Har bir testdan oldin ishga tushadigan sozlamalar
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(new BigDecimal("100.00"))
                .stock(10)
                .isActive(true)
                .build();

        OrderItemRequest orderItemRequest = new OrderItemRequest();
        orderItemRequest.setProductId(1L);
        orderItemRequest.setQuantity(2);

        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setCustomerName("John Doe");
        createOrderRequest.setCustomerEmail("john.doe@example.com");
        createOrderRequest.setOrderItems(Collections.singletonList(orderItemRequest));
    }

    @Test
    void createOrder_whenStockIsSufficient_shouldSucceed() {
        // Arrange (Tayyorgarlik)
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act (Amal)
        OrderResponse response = orderService.createOrder(createOrderRequest);

        // Assert (Tekshirish)
        assertNotNull(response);
        assertEquals(new BigDecimal("200.00"), response.getTotalAmount());
        assertEquals(1, response.getOrderItems().size());

        // ProductRepository'ning save metodi chaqirilganini tekshiramiz (stock kamayishi uchun)
        verify(productRepository, times(1)).save(any(Product.class));
        // OrderRepository'ning save metodi chaqirilganini tekshiramiz
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_whenProductNotFound_shouldThrowException() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> {
            orderService.createOrder(createOrderRequest);
        });

        // Hech qanday save metodi chaqirilmaganini tekshiramiz
        verify(orderRepository, never()).save(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void createOrder_whenStockIsInsufficient_shouldThrowException() {
        // Arrange
        product.setStock(1); // Omborda atigi 1 ta mahsulot bor
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act & Assert
        assertThrows(InsufficientStockException.class, () -> {
            orderService.createOrder(createOrderRequest);
        });

        // Hech qanday save metodi chaqirilmaganini tekshiramiz
        verify(orderRepository, never()).save(any());
    }
}