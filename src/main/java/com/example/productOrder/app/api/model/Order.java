package com.example.productOrder.app.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId; // ID objednávky

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderProduct> shoppingList; // Zoznam položiek objednávky

    private boolean paid; // Určuje, či bola objednávka zaplatená

    public void addProduct(OrderProduct orderProduct) {
        shoppingList.add(orderProduct);
        orderProduct.setOrder(this);
    }

    public void removeProduct(OrderProduct orderProduct) {
        shoppingList.remove(orderProduct);
        orderProduct.setOrder(null);
    }



    // Konstruktory
    public Order() {}

    public Order(boolean paid) {
        this.paid = paid;
    }

    // Gettery a settery
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

//    public List<OrderProduct> getShoppingList() {
//        return shoppingList;
//    }

    public void setShoppingList(List<OrderProduct> shoppingList) {
        this.shoppingList = shoppingList;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    @JsonIgnore
    public List<OrderProduct> getOrderProducts() {
        return shoppingList;
    }

}