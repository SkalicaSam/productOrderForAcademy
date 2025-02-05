package com.example.productOrder.app;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


//    @GetMapping
//    public List<Product> getAllProduct() {
//        return productService.getAllProduct();
//    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(
            @RequestParam(required = false, defaultValue = "")
            String searchText
    ) {
        return ResponseEntity.ok(productService.getAllProduct(searchText));
    }


    @PostMapping
    public ResponseEntity<Product> addProduct(@Valid @RequestBody ProductDTOAddProduct productDTOAddProduct) {
        Product newProduct = productService.addProduct(
                productDTOAddProduct.getName(),
                productDTOAddProduct.getDescription(),
                productDTOAddProduct.getAmount(),
                productDTOAddProduct.getPrice()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)  // 201
                .header("Description", "Product created")
                .body(newProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.OK).body("Order deleted");
    }

//    @DeleteMapping("/{id}")
//    public void deleteProduct(@PathVariable Integer id) {
//        productService.deleteProduct(id);
//    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> editProduct(@PathVariable Integer id, @RequestBody ProductDTOEdit productDTOEdit) {
        Product productUpdated = productService.getProductById(id);
        productUpdated.setName(productDTOEdit.getName());
        productUpdated.setDescription(productDTOEdit.getDescription());
        productService.saveProduct(productUpdated);

        return ResponseEntity
                .status(HttpStatus.OK)  // 200
                .header("Description", "Product updated")
                .body(productUpdated);
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Integer id) {
        return productService.getProductById(id);
    }





    // DTO  ProductDTOAmount
    @GetMapping("/{id}/amountDTO")
    public ProductDTOAmount getAmountOfProductByIdDTO(@PathVariable Integer id) {
        Product product = productService.getProductById(id);
        ProductDTOAmount productDTOAmount = new ProductDTOAmount();
        productDTOAmount.setAmount(product.getAmount());
        return productDTOAmount;
    }

    @PostMapping("/{id}/amountDTO")
    public ProductDTOAmount setAmountNumberOfProductByIdDTO(@PathVariable Integer id, @RequestBody ProductDTOAmount productDTOAmount) {
        Product product = productService.setAmountOfProduct(id, productDTOAmount.getAmount());
        return productDTOAmount;
    }

//    // to delete
//    @GetMapping("/{id}/amount")  // return only number format, not json format, of amount
//    public Integer getAmountOfProductById(@PathVariable Integer id) {
//        return productService.getAmountOfProduct(id);
//    }
//
//    @PostMapping("/{id}/amount")    // return only number format, not json format, of amount
//    public ResponseEntity<Map<String, Integer>> setAmountOfProductById(@PathVariable Integer id, @RequestBody Integer amount) {
//        Product product = productService.setAmountOfProduct(id, amount);
//        Map<String, Integer> response = new HashMap<>();
//        response.put("amount", product.getAmount());
//        return ResponseEntity.ok(response);
//    }


}
