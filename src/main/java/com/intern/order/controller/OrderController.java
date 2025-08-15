package com.intern.order.controller;

import com.intern.order.dto.CreateOrderRequest;
import com.intern.order.dto.OrderResponse;
import com.intern.order.enums.OrderStatus;
import com.intern.order.service.OrderService;
import com.intern.order.validation.annotations.ValueOfEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "02. Orders", description = "API for managing customer orders")
@Validated
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Create a new order (Public)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body (e.g., validation error, duplicate products)"),
            @ApiResponse(responseCode = "404", description = "Product in order not found"),
            @ApiResponse(responseCode = "409", description = "Conflict (e.g., insufficient stock)")
    })
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse createdOrder = orderService.createOrder(request);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all orders (ADMIN only)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @Operation(summary = "Get an order by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found with the given ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@Parameter(description = "ID of the order to retrieve", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @Operation(summary = "Get all orders for a specific customer by email")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved a list of orders for the customer")
    @GetMapping("/customer/{email}")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomerEmail(@Parameter(description = "Email address of the customer", example = "john.doe@example.com") @PathVariable String email) {
        return ResponseEntity.ok(orderService.getOrdersByCustomerEmail(email));
    }

    @Operation(summary = "Update the status of an order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status transition"),
            @ApiResponse(responseCode = "404", description = "Order not found with the given ID")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @Parameter(description = "ID of the order to update", example = "1") @PathVariable Long id,
            @Parameter(description = "The new status for the order (e.g., CONFIRMED, SHIPPED)")
            @ValueOfEnum(enumClass = OrderStatus.class, message = "Invalid status provided")
            @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, OrderStatus.valueOf(status.toUpperCase())));
    }

    @Operation(summary = "Cancel an order by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order cancelled successfully and stock restored"),
            @ApiResponse(responseCode = "400", description = "Order cannot be cancelled in its current state"),
            @ApiResponse(responseCode = "404", description = "Order not found with the given ID")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@Parameter(description = "ID of the order to cancel", example = "1") @PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }
}