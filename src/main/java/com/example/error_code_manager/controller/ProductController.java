package com.example.error_code_manager.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.error_code_manager.dto.ProductDTO;
import com.example.error_code_manager.entity.Product;
import com.example.error_code_manager.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product", description = "Product management APIs")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieves a list of all products")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    })
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get product by ID", description = "Retrieves a specific product by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the product"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Product> getProductById(@PathVariable Integer productId) {
        Optional<Product> product = productService.getProductByID(productId);
        return product.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @Operation(summary = "Create a new product", description = "Creates a new product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product successfully created")
    })
    public ResponseEntity<Product> createProduct(@RequestBody ProductDTO productDTO) {
        // Convert DTO to entity
        Product product = new Product();
        product.setName(productDTO.getName());
        
        Product createdProduct = productService.createProduct(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @PutMapping("/{productId}")
    @Operation(summary = "Update a product", description = "Updates an existing product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product successfully updated"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Product> updateProduct(@PathVariable Integer productId, 
                                               @RequestBody ProductDTO productDTO) {
        Optional<Product> existingProductOptional = productService.getProductByID(productId);
        
        if (existingProductOptional.isPresent()) {
            Product existingProduct = existingProductOptional.get();
            existingProduct.setName(productDTO.getName());
            
            Product updatedProduct = productService.createProduct(existingProduct);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Delete a product", description = "Deletes a product by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Product successfully deleted")
    })
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer productId) {
        productService.deleteProduct(productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}