package dev.sokarau.client.app.controllers;

import dev.sokarau.client.app.model.Order;
import dev.sokarau.client.app.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    // Create order
    @PostMapping()
    public ResponseEntity<String> createOrder(@RequestBody Map<String, String> payload) {
        return new ResponseEntity<>(orderService.createOrder(payload.get("name")), HttpStatus.CREATED);
    }

    // Get all orders (for testing purposes)
    @GetMapping()
    public ResponseEntity<List<Order>> getAllOrders() {
        return new ResponseEntity<>(orderService.allOrders(), HttpStatus.OK);
    }

    // Get order by correlationId (for testing purposes)
    @GetMapping("/{correlationId}")
    public ResponseEntity<Optional<Order>> getOrderByCorrelationId(@PathVariable String correlationId) {
        return new ResponseEntity<>(orderService.singleOrder(correlationId), HttpStatus.OK);
    }
}
