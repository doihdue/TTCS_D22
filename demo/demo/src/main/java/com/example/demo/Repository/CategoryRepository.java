package com.example.demo.Repository;

import com.example.demo.Model.Category;
import com.example.demo.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Product> findOneByName(String name);
}
