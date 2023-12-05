package com.example.springbootmall.dao.impl;

import com.example.springbootmall.dao.ProductDao;
import com.example.springbootmall.dto.ProductQueryParams;
import com.example.springbootmall.dto.ProductRequest;
import com.example.springbootmall.model.Product;
import com.example.springbootmall.rowmapper.ProductRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ProductDaoImpl implements ProductDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Product getProductId(Integer productId) {
        String sql = "SELECT  product_id,product_name, category, image_url, price, stock, description, " +
                "created_date, last_modified_date " +
                "FROM product WHERE product_id = :productId";
        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);
        List<Product> productList = namedParameterJdbcTemplate.query(sql, map, new ProductRowMapper());
        if (!productList.isEmpty()) {
            return productList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Integer createProduct(ProductRequest productRequest) {
        //enum java.sql.SQLException: Incorrect string value: '\xAC\xED\x00\x05~r...' for column 'category' at row 1
        //自行新增string解決此問題
        String s = productRequest.getCategory().name();
        String sql = "INSERT INTO product (product_name, category, image_url, price, stock, description, " +
                "created_date, last_modified_date) " +
                "VALUES (:productName,'" + s + "',:imageUrl,:price,:stock,:description," +
                ":createdDate,:lastModifiedDate);";
        Map<String, Object> map = new HashMap<>();
        map.put("productName", productRequest.getProductName());
        map.put("category", productRequest.getCategory());
        map.put("imageUrl", productRequest.getImageUrl());
        map.put("price", productRequest.getPrice());
        map.put("stock", productRequest.getStock());
        map.put("description", productRequest.getDescription());

        Date now = new Date();
        map.put("createdDate", now);
        map.put("lastModifiedDate", now);

//        查看map內容
//        System.out.println("內容：" + new MapSqlParameterSource(map));
//        for(String Key: map.keySet()){
//            System.out.println(Key + " " + map.get(Key));
//        }
//        System.out.println(map.get("category").getClass());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);
        int productId;
        productId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        return productId;
    }

    @Override
    public void updateProduct(Integer productId, ProductRequest productRequest) {
        String sql = "UPDATE product SET product_name = :productName, category= :category, image_url= :imageUrl," +
                "price = :price, stock=:stock, description=:description, last_modified_date=:lastModifiedDate" +
                " WHERE product_id=:productId";
        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);
        map.put("productName", productRequest.getProductName());
        map.put("category", productRequest.getCategory().toString()); //多了toString
        map.put("imageUrl", productRequest.getImageUrl());
        map.put("price", productRequest.getPrice());
        map.put("stock", productRequest.getStock());
        map.put("description", productRequest.getDescription());
        map.put("lastModifiedDate", new Date());
        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public void deleteProductById(Integer productId) {
        String sql = "DELETE FROM product WHERE product_id=:productId";
        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);
        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public List<Product> getProducts(ProductQueryParams productQueryParams) {
        String sql = "SELECT product_id,product_name,category,image_url,price,stock,description," +
                "created_date,last_modified_date FROM product WHERE 1=1";
        Map<String, Object> map = new HashMap<>();
        if (productQueryParams.getCategory() != null) {
            sql = sql + " AND category=:category";
            map.put("category", productQueryParams.getCategory().name()); //enum要使用.name()
        }
        if (productQueryParams.getSearch() != null) {
            sql = sql + " AND product_name LIKE :search";
            map.put("search", "%" + productQueryParams.getSearch() + "%"); //LIKE寫法，%要加在這裡而不能寫在上面
        }
        sql = sql + " ORDER BY " + productQueryParams.getOrderBy() + " " + productQueryParams.getSort();
        List<Product> productList = namedParameterJdbcTemplate.query(sql, map, new ProductRowMapper());
        return productList;
    }
}
