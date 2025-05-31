package com.example.demo.controller;


import com.example.demo.Model.ReviewProduct;
import com.example.demo.Service.Dto.ProductDto;
import com.example.demo.Service.ProductService;
import com.example.demo.Service.ReviewProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewProductService reviewProductService;

    @GetMapping("/gioithieu")
    public String gioithieu() {
        return "gioi-thieu";
    }

    @GetMapping("/lienhe")
    public String lienhe() {
        return "lien-he";
    }

    @GetMapping("/chinhsachgiaohang")
    public String chinhsachgiaohang() {
        return "chinh-sach-giao-hang";
    }

    @GetMapping("/") // Hoặc đường dẫn trang chủ của bạn
    public String homePage(Model model) {
        // Lấy 4 sản phẩm nổi bật
        List<ProductDto> featuredProducts = productService.getFeaturedProducts(4);
        model.addAttribute("featuredProducts", featuredProducts);

        // Lấy 3 đánh giá mới nhất
        List<ReviewProduct> testimonials = reviewProductService.getLatestReviews(3);
        model.addAttribute("testimonials", testimonials);

        return "index"; // Tên file HTML của bạn (ví dụ: index.html)
    }

}

