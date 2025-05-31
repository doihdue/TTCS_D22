package com.example.demo.controller;

import com.example.demo.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SearchApiController {

    private final ProductService productService;

    @Autowired
    public SearchApiController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/search-suggestions")
    public List<String> getSearchSuggestions(@RequestParam("term") String term) {
        return productService.getSuggestions(term, 5);
    }
}
