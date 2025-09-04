package com.cows.service;

import com.cows.entity.Product;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

/**
 * 商品信息服务接口
 * */
public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(int id);
    int addProduct(Product product);
    int updateProduct(Product product);
    int deleteProduct(int id);
//    List<Product> getProductsPaged(int page, int size, String sortField);
    Map<String, Object> getProductsPaged(int page, int size, String sortField);
    List<Product> searchProductsByName(String name);
}