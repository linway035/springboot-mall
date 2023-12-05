package com.example.springbootmall.service;

import com.example.springbootmall.constant.ProductCategory;
import com.example.springbootmall.dto.ProductRequest;
import com.example.springbootmall.model.Product;

import java.util.List;

public interface ProductService {
    Product getProductById(Integer productId);

    Integer createProduct(ProductRequest productRequest);

    void updateProduct(Integer productId, ProductRequest productRequest);

    void deleteProductById(Integer productId);

    List<Product> getProducts(ProductCategory category,String search);
}