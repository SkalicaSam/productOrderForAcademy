package com.example.productOrder.app;

import com.example.productOrder.app.api.exception.InternalErrorException;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProduct(String searchText){
        try {
            return productRepository.findAllProducts(searchText);
        } catch (Exception e) {
            throw new InternalErrorException("Error while finding products: " );
        }
    }

    public Product addProduct(String name, String description, Integer amount, Integer price){
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setAmount(amount);
        product.setPrice(price);
        productRepository.save(product);
        return product;
    }

    public void deleteProduct(Integer id){
        productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with ID " + id + " not found"));
        productRepository.deleteById(id);
    }

    public void editProduct(Integer id, String name, String description, Integer amount, Integer price ){
        productRepository.findById(id).ifPresent(product -> {
            product.setName(name);
            product.setDescription(description);
            product.setAmount(amount);
            product.setPrice(price);
            productRepository.save(product);
        });
    }

    public Product getProductById(Integer id) {
        return  productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with ID " + id + " not found"));
    }

    public Boolean productExistById(Integer id) {
        return  productRepository.findById(id).isPresent();
    }

    public Integer getAmountOfProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with ID " + id + " not found"));
        return product.getAmount();
    }

    public Product setAmountOfProduct(Integer id, Integer amount) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with ID " + id + " not found"));
        product.setAmount(amount);
        productRepository.save(product);

        return product;

    }

    public void saveProduct(Product product) {
        productRepository.save(product);
    }
}
