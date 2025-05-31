package com.example.demo.Service;

import com.example.demo.Model.ReviewProduct;
import com.example.demo.Repository.ReviewProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewProductService {

    @Autowired
    private ReviewProductRepository reviewProductRepository;



    public void save(ReviewProduct reviewProduct) {
        reviewProductRepository.save(reviewProduct);
    }
    //public long countByProductId(Long productId) {
    //return reviewProductRepository.countByProductId(productId);
    //}

    // Trong ReviewProductService.java
    public List<ReviewProduct> getLatestReviews(int limit) {
        // Logic để lấy 'limit' đánh giá mới nhất
        Pageable pageable = PageRequest.of(0, limit, Sort.by("date").descending()); // Sắp xếp theo ngày giảm dần
        return reviewProductRepository.findAll(pageable).getContent();
    }
}

