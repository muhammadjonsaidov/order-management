package com.intern.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intern.order.dto.CreateOrderRequest;
import com.intern.order.dto.OrderItemRequest;
import com.intern.order.entity.Order;
import com.intern.order.entity.Product;
import com.intern.order.enums.OrderStatus;
import com.intern.order.repository.OrderRepository;
import com.intern.order.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Product product1;
    private Order order1;

    @BeforeEach
    void setUp() {
        // Ma'lumotlarni tozalash (garchi @Transactional buni qilsa ham, aniqlik uchun)
        orderRepository.deleteAll();
        productRepository.deleteAll();

        // Testlar uchun umumiy mahsulot yaratamiz
        product1 = productRepository.save(Product.builder()
                .name("Test Laptop")
                .price(new BigDecimal("1200.00"))
                .stock(10)
                .category("Electronics")
                .isActive(true)
                .build());

        // Testlar uchun umumiy buyurtma yaratamiz va uni klass maydoniga saqlaymiz
        this.order1 = orderRepository.save(Order.builder()
                .customerName("Test Customer")
                .customerEmail("test@example.com")
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .totalAmount(new BigDecimal("1200.00"))
                .build());
    }

    @Test
    void createOrder_withValidData_shouldReturn201Created() throws Exception {
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(product1.getId());
        itemRequest.setQuantity(1);

        CreateOrderRequest orderRequest = new CreateOrderRequest();
        orderRequest.setCustomerName("Jane Doe");
        orderRequest.setCustomerEmail("jane.doe@example.com");
        orderRequest.setOrderItems(List.of(itemRequest));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.customerEmail").value("jane.doe@example.com"))
                .andExpect(jsonPath("$.totalAmount").value(1200.00));
    }

    @Test
    void createOrder_withInsufficientStock_shouldReturn409Conflict() throws Exception {
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(product1.getId());
        itemRequest.setQuantity(11);

        CreateOrderRequest orderRequest = new CreateOrderRequest();
        orderRequest.setCustomerName("Jane Doe");
        orderRequest.setCustomerEmail("jane.doe@example.com");
        orderRequest.setOrderItems(List.of(itemRequest));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void getOrderById_whenOrderExists_shouldReturn200OK() throws Exception {
        mockMvc.perform(get("/api/orders/" + this.order1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(this.order1.getId().intValue())))
                .andExpect(jsonPath("$.customerEmail", is("test@example.com")));
    }

    @Test
    void getAllOrders_shouldReturnOrderList() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(this.order1.getId().intValue())));
    }

    @Test
    void updateOrderStatus_whenOrderIsPending_shouldReturn200OK() throws Exception {
        mockMvc.perform(put("/api/orders/{id}/status", this.order1.getId())
                        .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CONFIRMED")));
    }

    @Test
    void updateOrderStatus_whenOrderIsNotPending_shouldReturn400BadRequest() throws Exception {
        this.order1.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(this.order1);

        mockMvc.perform(put("/api/orders/{id}/status", this.order1.getId())
                        .param("status", "SHIPPED"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cancelOrder_shouldUpdateStatusAndReturnStock() throws Exception {
        mockMvc.perform(delete("/api/orders/" + this.order1.getId()))
                .andExpect(status().isNoContent());

        Order cancelledOrder = orderRepository.findById(this.order1.getId()).get();
        assertEquals(OrderStatus.CANCELLED, cancelledOrder.getStatus());
    }
}
