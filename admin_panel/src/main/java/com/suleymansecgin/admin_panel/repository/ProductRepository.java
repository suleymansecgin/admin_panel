package com.suleymansecgin.admin_panel.repository;

import com.suleymansecgin.admin_panel.model.Product;
import com.suleymansecgin.admin_panel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByUser(User user);
    
    Optional<Product> findByIdAndUser(Long id, User user);
    
    boolean existsByIdAndUser(Long id, User user);
}

