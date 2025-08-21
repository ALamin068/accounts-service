package com.JDMGod.accounts_service.service;

import com.JDMGod.accounts_service.api.dto.ProductCreateRequest;
import com.JDMGod.accounts_service.model.Account;
import com.JDMGod.accounts_service.model.Product;
import com.JDMGod.accounts_service.repo.AccountRepository;
import com.JDMGod.accounts_service.repo.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final AccountRepository accountRepository;

    public ProductService(ProductRepository productRepository, AccountRepository accountRepository) {
        this.productRepository = productRepository;
        this.accountRepository = accountRepository;
    }

    public Page<Product> getProducts(Pageable pageable, String category, String search) {
        if (search != null && !search.trim().isEmpty()) {
            return productRepository.findByNameContainingAndStatus(search, "ACTIVE", pageable);
        } else if (category != null && !category.trim().isEmpty()) {
            return productRepository.findByCategoryAndStatus(category, "ACTIVE", pageable);
        } else {
            return productRepository.findByStatus("ACTIVE", pageable);
        }
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product createProduct(ProductCreateRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account seller = (Account) auth.getPrincipal();

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(request.getCategory());
        product.setImageUrl(request.getImageUrl());
        product.setSeller(seller);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, ProductCreateRequest request) {
        Product product = getProductById(id);
        
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(request.getCategory());
        product.setImageUrl(request.getImageUrl());
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        product.setStatus("DELETED");
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    public List<Product> getProductsBySeller() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account seller = (Account) auth.getPrincipal();
        return productRepository.findBySellerId(seller.getId());
    }

    public boolean isProductOwner(Long productId, String email) {
        Product product = getProductById(productId);
        return product.getSeller().getEmail().equals(email);
    }
}
