package com.example.productOrder.app.api.dto;

import jakarta.validation.constraints.Min;

public class ProductDTOAddProduct {
    private String name;
    private String description;
    private Integer amount;
    @Min(value = 1, message = "Price must be greater than zero")
    private Integer price;

    public ProductDTOAddProduct(String name, String description, Integer amount, Integer price) {
        this.name = name;
        this.description = description;
        this.amount = amount;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
