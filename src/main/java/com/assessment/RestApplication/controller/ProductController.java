package com.assessment.RestApplication.controller;

import java.lang.StackWalker.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assessment.RestApplication.model.Product;
import com.assessment.RestApplication.repository.ProductRepo;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepo productRepo;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        try {
            List<Product> productList = new ArrayList<>();
            productRepo.findAll().forEach(productList::add);

            if (productList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(productList, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getProductById(@PathVariable Long id) {
        try {
            Optional<Product> product = productRepo.findById(id);

            if (product.isPresent()) {
                return new ResponseEntity<>(product.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Product with ID " + id + " not found", HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<Object> addProduct(@RequestBody Product prod) {
        try {
            if (prod.getName() == null || prod.getName().isEmpty()) {
                return new ResponseEntity<>("Product name cannot be null or empty", HttpStatus.BAD_REQUEST);
            }
            if (prod.getPrice() <= 0) {
                return new ResponseEntity<>("Product price must be a positive value", HttpStatus.BAD_REQUEST);
            }

            Product prodObj = productRepo.save(prod);
            return new ResponseEntity<>(prodObj, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateProductById(@PathVariable Long id, @RequestBody Product prod) {
        try {
            Optional<Product> existingProduct = productRepo.findById(id);

            if (existingProduct.isPresent()) {
                Product updatedProduct = existingProduct.get();

                if (prod.getName() == null || prod.getName().isEmpty()) {
                    return new ResponseEntity<>("Product name cannot be null or empty", HttpStatus.BAD_REQUEST);
                }
                if (prod.getPrice() <= 0) {
                    return new ResponseEntity<>("Product price must be a positive value", HttpStatus.BAD_REQUEST);
                }

                updatedProduct.setName(prod.getName());
                updatedProduct.setDescription(prod.getDescription());
                updatedProduct.setPrice(prod.getPrice());

                productRepo.save(updatedProduct);
                return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Product with ID " + id + " not found", HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable Long id) {
        try {
            if (productRepo.existsById(id)) {
                productRepo.deleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>("Product with ID " + id + " not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
