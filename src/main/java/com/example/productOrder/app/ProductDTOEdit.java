package com.example.productOrder.app;

public class ProductDTOEdit {
    private String name;
    private String description;

    public ProductDTOEdit(String updatedName, String updatedDescription) {
    }

    public ProductDTOEdit() {
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
}
