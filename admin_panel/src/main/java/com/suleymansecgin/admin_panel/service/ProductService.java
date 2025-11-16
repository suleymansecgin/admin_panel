package com.suleymansecgin.admin_panel.service;

import com.suleymansecgin.admin_panel.dto.ProductRequest;
import com.suleymansecgin.admin_panel.dto.ProductResponse;
import com.suleymansecgin.admin_panel.exception.BaseException;
import com.suleymansecgin.admin_panel.exception.ErrorMessage;
import com.suleymansecgin.admin_panel.exception.MessageType;
import com.suleymansecgin.admin_panel.model.Product;
import com.suleymansecgin.admin_panel.model.User;
import com.suleymansecgin.admin_panel.repository.ProductRepository;
import com.suleymansecgin.admin_panel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public ProductResponse createProduct(ProductRequest request, String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION, 
                            "Kullanıcı bulunamadı")));
            
            Product product = new Product();
            product.setProductName(request.getProductName());
            product.setCostPrice(request.getCostPrice());
            product.setSellPrice(request.getSellPrice());
            product.setStockQuantity(request.getStockQuantity());
            product.setUser(user);
            
            product = productRepository.save(product);
            
            return convertToResponse(product);
        } catch (Exception e) {
            throw new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION, 
                    "Ürün kaydedilirken hata oluştu: " + e.getMessage()));
        }
    }
    
    public List<ProductResponse> getAllProducts(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION, 
                        "Kullanıcı bulunamadı")));
        
        return productRepository.findByUser(user)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public ProductResponse getProductById(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION, 
                        "Kullanıcı bulunamadı")));
        
        Product product = productRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.PRODUCT_NOT_FOUND, "ID: " + id)));
        return convertToResponse(product);
    }
    
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION, 
                        "Kullanıcı bulunamadı")));
        
        Product product = productRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.PRODUCT_NOT_FOUND, "ID: " + id)));
        
        product.setProductName(request.getProductName());
        product.setCostPrice(request.getCostPrice());
        product.setSellPrice(request.getSellPrice());
        product.setStockQuantity(request.getStockQuantity());
        
        product = productRepository.save(product);
        
        return convertToResponse(product);
    }
    
    @Transactional
    public void deleteProduct(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION, 
                        "Kullanıcı bulunamadı")));
        
        if (!productRepository.existsByIdAndUser(id, user)) {
            throw new BaseException(new ErrorMessage(MessageType.PRODUCT_NOT_FOUND, "ID: " + id));
        }
        productRepository.deleteById(id);
    }
    
    private ProductResponse convertToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getProductName(),
                product.getCostPrice(),
                product.getSellPrice(),
                product.getStockQuantity(),
                product.getCreateTime()
        );
    }
}

