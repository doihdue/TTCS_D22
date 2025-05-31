package com.example.demo.Repository;

import com.example.demo.Model.Category;
import com.example.demo.Model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {


    long countByStockLessThan(int stock);

    // Đếm số sản phẩm có giá cũ
    long countByOldPriceGreaterThan(double oldPrice);

    @Query("SELECT p FROM Product p " +
            "WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.decriptionShort) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.desciptionDetail) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.category.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR CAST(p.price AS string) LIKE CONCAT('%', :keyword, '%') " +
            "OR CAST(p.voucherPrice AS string) LIKE CONCAT('%', :keyword, '%')")
    List<Product> searchProducts(@Param("keyword") String keyword);


    // Tìm kiếm gợi ý (autocomplete)
    @Query("SELECT DISTINCT p.productName FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :term, '%')) ORDER BY p.productName")
    List<String> findSuggestions(@Param("term") String term, Pageable pageable);



    // Tìm sản phẩm theo Category, loại trừ một ProductID cụ thể, và có phân trang (để lấy limit)
    List<Product> findByCategoryAndProductIDNot(Category category, Long productID, Pageable pageable);
}
