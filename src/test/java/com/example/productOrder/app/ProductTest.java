package com.example.productOrder.app;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void productEdit_returnTrue3() {
        Integer productId = 1;
        final ProductDTOEdit editRequest = new ProductDTOEdit();
        editRequest.setName("Updated Name");
        editRequest.setDescription("Updated Description");
        System.out.println(editRequest.getDescription() + "*   *    *     *  ");

        final ResponseEntity<Product> response = restTemplate.exchange(
                "/product/{productId}", // Ensure this matches your endpoint URL
                HttpMethod.PUT,
                new HttpEntity<>(editRequest),
                Product.class,
                productId
        );

        // Ověření odpovědi
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Updated Name", response.getBody().getName());
        Assertions.assertEquals("Updated Description", response.getBody().getDescription());
        Assertions.assertEquals("Product updated", response.getHeaders().getFirst("Description"));

        System.out.println("* * Produkt s ID " + productId + "  ; " + response.getBody().getName() +  " existuje a bol Updatovany * * ");
    }
}
