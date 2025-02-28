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

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, OrderProductRepository orderProductRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
        this.productRepository = productRepository;
    }

    public Order createOrder(Order order) {
        if (order.getOrderProducts() != null) {
            // Check if there is enough stock for all items
            for (OrderProduct item : order.getOrderProducts()) {
                Product product = productRepository.findById(item.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));
                if (product.getAmount() < item.getAmount()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough stock for product ID " + product.getId());
                }
            }

            // Update product amounts if there is enough stock
            order.getOrderProducts().forEach(item ->{

                Product product = productRepository.findById(item.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));
//                if(product.getAmount() < item.getAmount()){
//                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough stock for product ID " + product.getId());
//                }
                product.setAmount(product.getAmount() - item.getAmount());
                productRepository.save(product);
                item.setOrder(order);
            } );
        }
//        if (order.getShoppingList() != null) {
//            order.getShoppingList().forEach(item -> item.setOrder(order));
//        }
        order.setPaid(false);
        return orderRepository.save(order);
    }

    // this is not using. We are using addProductToOrderDTO !!!
    // Pridanie produktu do objednávky
    public void addProductToOrder(Integer orderId, Integer productId, Integer amount) {
        Order order = orderRepository.findById(Long.valueOf(orderId))
                .orElseThrow(() -> new RuntimeException("Order not found"));
        // Hledáme, zda už produkt v objednávce existuje
        Optional<OrderProduct> existingOrderProduct = order.getOrderProducts().stream()
                .filter(op -> op.getProductId().equals(productId))
                .findFirst();

        if (existingOrderProduct.isPresent()) {
            // Produkt už existuje – aktualizujeme množství
            OrderProduct orderProduct = existingOrderProduct.get();
            orderProduct.setAmount(orderProduct.getAmount() + amount);
            orderProductRepository.save(orderProduct);
        } else {
            // Produkt neexistuje – přidáme nový záznam
            OrderProduct orderProduct = new OrderProduct(productId, amount, order);
            order.addProduct(orderProduct);
            orderProductRepository.save(orderProduct);
        }


//        OrderProduct orderProduct = new OrderProduct( productId, amount, order);
//        order.addProduct(orderProduct);  // Pridáme produkt do objednávky
//        orderProductRepository.save(orderProduct);  // Uložíme produkt
    }


    // Odstránenie produktu z objednávky
    public void removeProductFromOrder(Long orderId, Long productId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        OrderProduct orderProduct = order.getOrderProducts().stream()
                .filter(op -> op.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found in order"));
        order.removeProduct(orderProduct);  // Odstránime produkt z objednávky
        orderProductRepository.delete(orderProduct);  // Zmažeme produkt
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);  // Tento príkaz vymaže objednávku a jej položky (vďaka CascadeType.ALL)
    }


    public void addProductToOrderWithDTO(Integer orderId, AddToOrderRequestDTO addToOrderRequestDTO) {

        Order order = orderRepository.findById(Long.valueOf(orderId))
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order with ID " + orderId + " not found"));

        if (order.isPaid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  "Your order is already paid. You cannot add the next product." );
        }

        Product product = productRepository.findById(addToOrderRequestDTO.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with ID " + addToOrderRequestDTO.getProductId() + " not found"));

        if (product.getAmount() < addToOrderRequestDTO.getAmount()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough stock for product ID " + addToOrderRequestDTO.getProductId());
        }

        // Najdeme produkt v objednávce
        Optional<OrderProduct> existingOrderProduct = order.getOrderProducts().stream()
                .filter(op -> op.getProductId().equals(addToOrderRequestDTO.getProductId()))
                .findFirst();

        if (existingOrderProduct.isPresent()) {
            // Produkt už existuje – aktualizujeme množství
            OrderProduct orderProduct = existingOrderProduct.get();
            orderProduct.setAmount(orderProduct.getAmount() + addToOrderRequestDTO.getAmount());
            orderProductRepository.save(orderProduct);
        } else {
            // Produkt neexistuje – přidáme nový záznam
            OrderProduct orderProduct = new OrderProduct(addToOrderRequestDTO.getProductId(), addToOrderRequestDTO.getAmount(), order);
            order.addProduct(orderProduct);
            orderProductRepository.save(orderProduct);
        }

        // Odečteme množství z celkového skladu
        product.setAmount(product.getAmount() - addToOrderRequestDTO.getAmount());
        productRepository.save(product);
    }






}


