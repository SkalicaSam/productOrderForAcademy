package com.example.productOrder.app;

import com.example.productOrder.app.api.exception.ProductNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductIntegrationTransactionalTest {


    private Integer productId;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private final ProductService productService;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    public ProductIntegrationTransactionalTest(ProductService productService) {
        this.productService = productService;
    }

    @Test
    void addProduct_shouldReturnProduct201CREATED_whenSendingDataAreCorect() {

        final ProductDTOAddProduct addRequest1 = new ProductDTOAddProduct(
                "projector",
                "lumnes 250",
                3,
                250
        );
        final ResponseEntity<Product> addProductResponse1 = restTemplate.postForEntity(
                "/product",
                addRequest1,
                Product.class
        );

        final Product createdProduct1 = addProductResponse1.getBody();
        Assertions.assertNotNull(createdProduct1);
        productId = createdProduct1.getId();
        Assertions.assertEquals(HttpStatus.CREATED, addProductResponse1.getStatusCode());

        final ResponseEntity<Product> response = restTemplate.exchange(
                "/product/{productId}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                },
                productId
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(createdProduct1.getId(), response.getBody().getId());
        Assertions.assertEquals(createdProduct1.getName(), response.getBody().getName());

        productService.deleteProduct(productId);
    }

    @Test
    void addProduct_shouldReturnProduct400BAD_REQUEST_whenSendingDataAreWrong() {
        // price:  @Min(value = 1, message = "Price must be greater than zero")

        final ProductDTOAddProduct addRequest1 = new ProductDTOAddProduct(
                "projector",
                null,
                -4,
                -250
        );
        final ResponseEntity<Product> addProductResponse1 = restTemplate.postForEntity(
                "/product",
                addRequest1,
                Product.class
        );

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, addProductResponse1.getStatusCode());

    }

    @Test
    void getProductById_shouldReturn200OK_whenProductExist() {
        Product productForTest = productService.addProduct("Test Product", "Test Description", 10, 100);
        Integer productId = productForTest.getId();

        final ResponseEntity<Product> getResponse = restTemplate.getForEntity(
                "/product/{productId}",
                Product.class,
                productId
        );

        Assertions.assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Assertions.assertNotNull(getResponse.getBody());

        final Product product = getResponse.getBody();
        Assertions.assertEquals(productId, product.getId());

        productService.deleteProduct(productId);
    }

    @Test
    void getProductById_shouldReturn404NOT_FOUND_whenProductNotExist() {
        final ResponseEntity<ProductNotFoundException> getResponse = restTemplate.getForEntity(
                "/product/9999",
                ProductNotFoundException.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    void editProduct_shouldReturnTrue3() {
        Integer productId = 3;
        final ProductDTOEdit editRequest = new ProductDTOEdit();
        editRequest.setName("Updated Name");
        editRequest.setDescription("Updated Description");

        final ResponseEntity<Product> response = restTemplate.exchange(
                "/product/{productId}",
                HttpMethod.PUT,
                new HttpEntity<>(editRequest),
                Product.class,
                productId
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Updated Name", response.getBody().getName());
        Assertions.assertEquals("Updated Description", response.getBody().getDescription());
        Assertions.assertEquals("Product updated", response.getHeaders().getFirst("Description"));
    }

    @Test
    public void deleteProduct_shouldReturn200OK() {
        Product productForTest = productService.addProduct("Test to delete Product", "Description", 10, 100);
        Integer productId = productForTest.getId();
        System.out.println(productId);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/product/" + productId,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        boolean productExists = productRepository.findById(productId).isPresent();
        assertThat(productExists).isFalse();
    }

    @Test
    public void deleteProduct_shouldReturn404NOTFOUND_whenProductNotExist() {

        final ResponseEntity<ProductNotFoundException> response = restTemplate.exchange(
                "/product/9999",
                HttpMethod.DELETE,
                null,
                ProductNotFoundException.class
        );
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getAmount_shouldReturn200OK_whenProductExist() {

        Integer productId = null;
        try {
            Product productForTest2 = productService.addProduct("Test to delete Product", "Description", 10, 100);
            productId = productForTest2.getId();

            ResponseEntity<ProductDTOAmount> response = restTemplate.getForEntity(
                    "/product/{productId}/amountDTO",
                    ProductDTOAmount.class,
                    productId
            );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertEquals(productForTest2.getAmount(), Objects.requireNonNull(response.getBody()).getAmount());

        } finally {
            productService.deleteProduct(productId);
        }
    }

    @Test
    public void getAmount_shouldReturn404NOTFOUND_whenProductNotExist() {

        ResponseEntity<ProductNotFoundException> response = restTemplate.getForEntity(
                "/product/9999/amountDTO",
                ProductNotFoundException.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void postAmount_shouldReturn200OK_whenProductExist() {

        Integer productId = null;
        try {
            Product productForTest2 = productService.addProduct("Test to delete Product", "Description", 10, 100);
            productId = productForTest2.getId();

            ProductDTOAmount sendingDTOAmount = new ProductDTOAmount(100);


            ResponseEntity<ProductDTOAmount> response = restTemplate.postForEntity(
                    "/product/{productId}/amountDTO",
                    sendingDTOAmount,
                    ProductDTOAmount.class,
                    productId
            );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertEquals(sendingDTOAmount.getAmount(), Objects.requireNonNull(response.getBody()).getAmount());
            Assertions.assertEquals(productService.getAmountOfProduct(productId), Objects.requireNonNull(response.getBody()).getAmount());

        } finally {
            productService.deleteProduct(productId);
        }
    }

    @Test
    public void postAmount_shouldReturn404NOTFOUND_whenProductNotExist() {

        ProductDTOAmount sendingDTOAmount = new ProductDTOAmount(100);
        ResponseEntity<ProductNotFoundException> response = restTemplate.postForEntity(
                "/product/9999/amountDTO",
                sendingDTOAmount,
                ProductNotFoundException.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


}
