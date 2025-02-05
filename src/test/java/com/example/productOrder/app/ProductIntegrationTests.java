package com.example.productOrder.app;

import com.example.productOrder.app.api.exception.ProductNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

//@Transactional this is example of testing in production environment, so without @Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductIntegrationTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getAllProducts_return_200OK() {  // Db must have 2 Products!!
        final ResponseEntity<List<Product>> response = restTemplate.exchange(
                "/product",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }   // return list of objects
        );

        Assertions.assertEquals(
                HttpStatus.OK, response.getStatusCode()  // 1 what I expect, 2 what was returned
        );
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody().size() >= 2);
    }

    @Test
    void Create2Products_test_getAllProducts_return_200OK() {
        //create 2 new products
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

        final ProductDTOAddProduct addRequest2 = new ProductDTOAddProduct(
                "projector",
                "lumnes 250",
                3,
                250
        );

        final ResponseEntity<Product> addProductResponse2 = restTemplate.postForEntity(
                "/product",
                addRequest2,
                Product.class
        );

        final Product createdProduct1 = addProductResponse1.getBody();
        Assertions.assertNotNull(createdProduct1);

        final Product createdProduct2 = addProductResponse2.getBody();
        Assertions.assertNotNull(createdProduct2);


        // create response for GET product
        try {
            final ResponseEntity<List<Product>> response = restTemplate.exchange(
                    "/product",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );
            Assertions.assertEquals(
                    HttpStatus.OK, response.getStatusCode()
            );
            Assertions.assertNotNull(response.getBody());
            Assertions.assertTrue(response.getBody().size() >= 2);
        } finally {
            // delete created 2 products
            final ResponseEntity<Void> deleteProductResponse = restTemplate.exchange(
                    "/product/" + createdProduct1.getId(),
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );
            final ResponseEntity<Void> deleteProductResponse2 = restTemplate.exchange(
                    "/product/" + createdProduct2.getId(),
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );

            System.out.println("Deleted Product ID: " + createdProduct1.getId());
            System.out.println("Deleted Product ID: " + createdProduct2.getId());
        }
    }

    @Test
    void getProduct_return_200OK() {
        final ResponseEntity<Product> getResponse = restTemplate.getForEntity(
                "/product/1",
                Product.class
        );

        Assertions.assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Assertions.assertNotNull(getResponse.getBody());

        final Product product = getResponse.getBody();
        Assertions.assertEquals(1, product.getId());
        Assertions.assertEquals("string", product.getName());
    }

    @Test
    void getNonExistingProduct_return_NOT_FOUND() {
        final ResponseEntity<ProductNotFoundException> getResponse = restTemplate.getForEntity(
                "/product/99",
                ProductNotFoundException.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    void createNewProduct_return_201Created() {
        final ProductDTOAddProduct addRequest = new ProductDTOAddProduct(
                "projector",
                "lumnes 250",
                3,
                250
        );

        final ResponseEntity<Product> addProductResponse = restTemplate.postForEntity(
                "/product",
                addRequest,
                Product.class
        );

        Assertions.assertEquals(HttpStatus.CREATED, addProductResponse.getStatusCode());


        final Product createdProduct = addProductResponse.getBody();
        Assertions.assertNotNull(createdProduct);

        try {
            final ResponseEntity<Product> getProductResponse = restTemplate.getForEntity(
                    "/product/" + createdProduct.getId(),
                    Product.class
            );
            Assertions.assertEquals(HttpStatus.OK, getProductResponse.getStatusCode());
            Assertions.assertNotNull(getProductResponse.getBody());
            Assertions.assertEquals(addRequest.getName(), getProductResponse.getBody().getName());
            Assertions.assertEquals(addRequest.getDescription(), getProductResponse.getBody().getDescription());
        } finally {
            final ResponseEntity<Void> deleteProductResponse = restTemplate.exchange(
                    "/product/" + createdProduct.getId(),
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );
        }
    }




}
