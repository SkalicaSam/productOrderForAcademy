package com.example.productOrder.app.api.dto;

public class ProductDTOAmount {
    private Integer amount;

    public ProductDTOAmount(Integer amount) {
        this.amount = amount;
    }

    public ProductDTOAmount() {
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
