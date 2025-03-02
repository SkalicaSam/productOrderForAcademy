package com.example.productOrder.app.api.controller;

import com.example.productOrder.app.api.dto.shoppingList.AddToOrderRequestDTO;
import com.example.productOrder.app.api.model.Order;
import com.example.productOrder.app.api.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        Order savedOrder = orderService.createOrder(order);
        return ResponseEntity.ok(savedOrder).getBody();
//        return orderService.createOrder(order);
    }


    // Pridanie produktu do objednávky
    @PostMapping("/{orderId}/addProductDTO")
    public ResponseEntity<String> addProductToOrderDTO(
            @PathVariable Integer orderId,
            @RequestBody AddToOrderRequestDTO addToOrderRequestDTO) {
        orderService.addProductToOrderWithDTO(orderId, addToOrderRequestDTO);
        return ResponseEntity.ok("Product added to order");
    }

    // Odstránenie produktu z objednávky
    @DeleteMapping("/{orderId}/product/{productId}")
    public ResponseEntity<String> removeProductFromOrder(
            @PathVariable Long orderId,
            @PathVariable Long productId) {
        orderService.removeProductFromOrder(orderId, productId);
        return ResponseEntity.ok("Product removed from order");
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @DeleteMapping("/order/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok("Order deleted successfully");
    }

    // Ďalšie API metódy podľa potreby
}


//    // Pridanie produktu do objednávky
//    @PostMapping("/{orderId}/addProduct")
//    public ResponseEntity<String> addProductToOrder(
//            @PathVariable Integer orderId,
//            @RequestParam Integer productId,
//            @RequestParam Integer amount) {
//        orderService.addProductToOrder(orderId, productId, amount);
//        return ResponseEntity.ok("Product added to order");
//    }
