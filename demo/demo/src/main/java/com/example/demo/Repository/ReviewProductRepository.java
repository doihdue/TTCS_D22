package com.example.demo.Repository;

import com.example.demo.Model.ReviewProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewProductRepository extends JpaRepository<ReviewProduct, Long> {

}

