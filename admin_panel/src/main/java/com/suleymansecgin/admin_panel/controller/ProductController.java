package com.suleymansecgin.admin_panel.controller;

import com.suleymansecgin.admin_panel.dto.ProductRequest;
import com.suleymansecgin.admin_panel.dto.ProductResponse;
import com.suleymansecgin.admin_panel.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        throw new RuntimeException("Kullanıcı kimlik doğrulaması yapılmamış");
    }
    
    @PostMapping(value = {"", "/"})
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        String username = getCurrentUsername();
        ProductResponse response = productService.createProduct(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping(value = {"", "/"})
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        String username = getCurrentUsername();
        List<ProductResponse> products = productService.getAllProducts(username);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        String username = getCurrentUsername();
        ProductResponse product = productService.getProductById(id, username);
        return ResponseEntity.ok(product);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        String username = getCurrentUsername();
        ProductResponse response = productService.updateProduct(id, request, username);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        String username = getCurrentUsername();
        productService.deleteProduct(id, username);
        return ResponseEntity.noContent().build();
    }
}

