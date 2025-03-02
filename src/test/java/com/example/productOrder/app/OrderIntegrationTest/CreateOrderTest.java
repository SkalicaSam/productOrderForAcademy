package com.example.productOrder.app.OrderIntegrationTest;

import com.example.productOrder.app.api.model.Order;
import com.example.productOrder.app.api.model.OrderProduct;
import com.example.productOrder.app.api.model.Product;
import com.example.productOrder.app.api.repository.OrderRepository;
import com.example.productOrder.app.api.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CreateOrderTest {

    private Integer product1Id;
    private Integer product2Id;
    private Order order;


    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        // Mazání pouze testovacích dat (např. podle názvu produktu)
        productRepository.findAll().stream()
                .filter(p -> p.getName().startsWith("Test Product"))
                .forEach(productRepository::delete);

//        orderRepository.deleteAll();

        Product product1 = new Product();
        product1.setName("Test Product 1");
        product1.setDescription("Description 1");
        product1.setAmount(10);
        product1.setPrice(100);
        productRepository.save(product1);
        this.product1Id = product1.getId();

        Product product2 = new Product();
        product2.setName("Test Product 2");
        product2.setDescription("Description 2");
        product2.setAmount(5);
        product2.setPrice(200);
        productRepository.save(product2);
//        Integer product2Id = product2.getId();
        this.product2Id = product2.getId();
    }


//    @Test
//    void createOrder_shouldReturnOrder201CREATED_whenSendingDataAreCorrect(){
//
//    }

    @Test
    public void testCreateOrder_shouldReturnOrder201CREATED_whenSendingDataAreCorrect() {
        Order order = new Order();

//        OrderProduct orderProduct1 = new OrderProduct(product1Id, 2, order );
//        OrderProduct orderProduct1 = new OrderProduct( 2, order.getOrderId(), product1Id );
        OrderProduct orderProduct1 = new OrderProduct( product1Id, 2,  order );

//        OrderProduct orderProduct2 = new OrderProduct(product2Id, 2, order );
        order.setPaid(false);

//        order.setShoppingList(orderProduct1);
        order.setShoppingList(List.of(orderProduct1));

//        order.setShoppingList(List.of(new OrderProduct(orderProduct1, 2)));

        Order savedOrder = orderRepository.save(order);

        assertNotNull(savedOrder);
        assertFalse(savedOrder.isPaid());
    }

    @AfterEach
    void tearDown() {
//        orderRepository.deleteAll();
//        orderRepository.delete(order);

        productRepository.findAll().stream()
                .filter(p -> p.getName().startsWith("Test Product"))
                .forEach(productRepository::delete);

    }

}
