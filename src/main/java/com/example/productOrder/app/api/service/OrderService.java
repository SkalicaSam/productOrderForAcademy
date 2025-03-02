package com.example.productOrder.app.api.service;

import com.example.productOrder.app.api.dto.shoppingList.AddToOrderRequestDTO;
import com.example.productOrder.app.api.model.Order;
import com.example.productOrder.app.api.model.OrderProduct;
import com.example.productOrder.app.api.model.Product;
import com.example.productOrder.app.api.repository.OrderProductRepository;
import com.example.productOrder.app.api.repository.OrderRepository;
import com.example.productOrder.app.api.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderProductRepository orderProductRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
        this.productRepository = productRepository;
    }


    public Order createOrder(Order order) {
        if (order.getOrderProducts() != null) {
            checkSufficientProductQuantity(order);
            updateProductAmountWhenOrdered(order);
        }
        order.setPaid(false);
        return orderRepository.save(order);
    }

    private void checkSufficientProductQuantity(Order order){
        for (OrderProduct item : order.getOrderProducts()) {
            Product product = productRepository.findById(item.getProductId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with ID " + item.getProductId() + " not found"));
            if (product.getAmount() < item.getAmount()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough stock for product ID " + product.getId());
            }
        }
    }

    private void updateProductAmountWhenOrdered(Order order) {
        order.getOrderProducts().forEach(item ->{
            Product product = productRepository.findById(item.getProductId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with ID " + item.getProductId() + " not found"));
            product.setAmount(product.getAmount() - item.getAmount());
            productRepository.save(product);
            item.setOrder(order);
        } );
    }

    public void removeProductFromOrder(Long orderId, Long productId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        OrderProduct orderProduct = order.getOrderProducts().stream()
                .filter(op -> op.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found in order"));
        order.removeProduct(orderProduct);
        orderProductRepository.delete(orderProduct);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    public void addProductToOrderWithDTO(Integer orderId, AddToOrderRequestDTO addToOrderRequestDTO) {
        Order order = findOrderById(orderId);
        checkOrderStatus(order);
        Product product = findProductById(addToOrderRequestDTO.getProductId());
        checkSufficientProductQuantity(product, addToOrderRequestDTO.getAmount());
        updateOrCreateOrderItem(order, product, addToOrderRequestDTO.getAmount());
        updateProductQuantity(product, addToOrderRequestDTO.getAmount());
    }

    private void checkOrderStatus(Order order) {
        if (order.isPaid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your order is already paid. You cannot add more products.");
        }
    }

    private Order findOrderById(Integer orderId) {
        return orderRepository.findById(Long.valueOf(orderId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order with ID " + orderId + " not found"));
    }

    private Product findProductById(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with ID " + productId + " not found"));
    }

    private void checkSufficientProductQuantity(Product product, Integer amount) {
        if (product.getAmount() < amount) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient stock for product ID " + product.getId());
        }
    }

    private void updateOrCreateOrderItem(Order order, Product product, Integer amount){
        Optional<OrderProduct> existingOrderProduct = order.getOrderProducts().stream()
                .filter(op -> op.getProductId().equals(product.getId()))
                .findFirst();

        if (existingOrderProduct.isPresent()) {
            OrderProduct orderProduct = existingOrderProduct.get();
            orderProduct.setAmount(orderProduct.getAmount() + amount);
            orderProductRepository.save(orderProduct);
        } else {
            OrderProduct orderProduct = new OrderProduct(product.getId(), amount, order);
            order.addProduct(orderProduct);
            orderProductRepository.save(orderProduct);
        }
    }

    private void updateProductQuantity(Product product, Integer amount) {
        product.setAmount(product.getAmount() - amount);
        productRepository.save(product);
    }


}


//    // Pridanie produktu do objednávky   // this is not using. We are using addProductToOrderDTO !!!
//    public void addProductToOrder(Integer orderId, Integer productId, Integer amount) {
//        Order order = orderRepository.findById(Long.valueOf(orderId))
//                .orElseThrow(() -> new RuntimeException("Order not found"));
//        // Hledáme, zda už produkt v objednávce existuje
//        Optional<OrderProduct> existingOrderProduct = order.getOrderProducts().stream()
//                .filter(op -> op.getProductId().equals(productId))
//                .findFirst();
//
//        if (existingOrderProduct.isPresent()) {
//            // Produkt už existuje – aktualizujeme množství
//            OrderProduct orderProduct = existingOrderProduct.get();
//            orderProduct.setAmount(orderProduct.getAmount() + amount);
//            orderProductRepository.save(orderProduct);
//        } else {
//            // Produkt neexistuje – přidáme nový záznam
//            OrderProduct orderProduct = new OrderProduct(productId, amount, order);
//            order.addProduct(orderProduct);
//            orderProductRepository.save(orderProduct);
//        }
//    }









