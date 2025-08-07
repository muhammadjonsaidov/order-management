package com.intern.order.service;

import com.intern.order.dto.CreateOrderRequest;
import com.intern.order.dto.OrderItemRequest;
import com.intern.order.dto.OrderItemResponse;
import com.intern.order.dto.OrderResponse;
import com.intern.order.entity.Order;
import com.intern.order.entity.OrderItem;
import com.intern.order.entity.Product;
import com.intern.order.enums.OrderStatus;
import com.intern.order.exception.InsufficientStockException;
import com.intern.order.exception.InvalidOrderStatusException;
import com.intern.order.exception.OrderNotFoundException;
import com.intern.order.exception.ProductNotFoundException;
import com.intern.order.repository.OrderRepository;
import com.intern.order.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating a new order for customer: {}", request.getCustomerEmail());

        validateNoDuplicateProducts(request.getOrderItems());

        Order newOrder = Order.builder()
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getOrderItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + itemRequest.getProductId()));

            if (product.getStock() < itemRequest.getQuantity()) {
                throw new InsufficientStockException("Not enough stock for product: " + product.getName() +
                        ". Available: " + product.getStock() + ", Requested: " + itemRequest.getQuantity());
            }

            BigDecimal unitPrice = product.getPrice();
            BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalAmount = totalAmount.add(totalPrice);

            OrderItem orderItem = OrderItem.builder()
                    .order(newOrder)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(unitPrice)
                    .totalPrice(totalPrice)
                    .build();

            newOrder.getOrderItems().add(orderItem);

            product.setStock(product.getStock() - itemRequest.getQuantity());
            productRepository.save(product);
        }

        newOrder.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(newOrder);

        log.info("Order created successfully with ID: {}", savedOrder.getId());
        return mapToOrderResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = findOrderById(id);
        return mapToOrderResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomerEmail(String email) {
        return orderRepository.findByCustomerEmail(email).stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatus newStatus) {
        log.info("Updating status for order ID: {} to {}", id, newStatus);
        Order order = findOrderById(id);

        // Qo'shimcha biznes qoidasi: Faqat PENDING statusidagi buyurtmani o'zgartirish mumkin
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException("Only orders with PENDING status can be modified. Current status: " + order.getStatus());
        }

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        log.info("Status for order ID: {} updated successfully to {}", id, newStatus);
        return mapToOrderResponse(updatedOrder);
    }

    @Transactional
    public void cancelOrder(Long id) {
        log.warn("Attempting to cancel order ID: {}", id);
        Order order = findOrderById(id);

        // Buyurtmani faqat ma'lum statuslarda bekor qilish mumkin (masalan, DELIVERED bo'lsa bekor qilib bo'lmaydi)
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderStatusException("Cannot cancel an order that is already " + order.getStatus());
        }

        // Qo'shimcha biznes qoidasi: Bekor qilinganda mahsulot sonini omborga qaytarish
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
            log.info("Returned {} unit(s) of product '{}' to stock.", item.getQuantity(), product.getName());
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        log.warn("Order ID: {} has been successfully cancelled.", id);
    }

    // --- Yordamchi metodlar ---

    private Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
    }

    private void validateNoDuplicateProducts(List<OrderItemRequest> items) {
        Set<Long> productIds = new HashSet<>();
        for (OrderItemRequest item : items) {
            if (!productIds.add(item.getProductId())) {
                throw new InvalidOrderStatusException("Duplicate product found in order: productId " + item.getProductId());
            }
        }
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getTotalPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .orderItems(itemResponses)
                .build();
    }
}