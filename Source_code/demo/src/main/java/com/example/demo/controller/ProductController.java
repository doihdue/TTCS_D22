package com.example.demo.controller;

import com.example.demo.Model.Product;
import com.example.demo.Model.ReviewProduct;
import com.example.demo.Model.User;
import com.example.demo.Service.Dto.ProductDto;
import com.example.demo.Service.ProductService;
import com.example.demo.Service.ReviewProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/")
public class ProductController {

    @Autowired
    ProductService productService;
    @Autowired
    ReviewProductService reviewProductService;

    @GetMapping("san-pham")
    public String sanPham(Model model) {
        List<ProductDto> productDtoList = productService.GetAllProduct();
        model.addAttribute("productDtoList", productDtoList);

        List<ProductDto> featuredProducts = productService.getFeaturedProducts(3);
        model.addAttribute("featuredProducts", featuredProducts);
        return "san-pham";
    }

    @GetMapping("/san-pham1")
    public String getProductsByCategory(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
            @RequestParam(value = "sort", required = false, defaultValue = "default") String sort,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            Model model) {

        List<ProductDto> productDtoList;

        // Xử lý tìm kiếm và lọc sản phẩm
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Tìm kiếm theo từ khóa
            productDtoList = productService.searchProducts(keyword, category, minPrice, maxPrice, sort);
            model.addAttribute("keyword", keyword);
        } else if (category != null && !category.trim().isEmpty()) {
            // Lọc theo danh mục
            productDtoList = productService.getProductsByCategory(category, minPrice, maxPrice, sort);
        } else {
            // Lấy tất cả sản phẩm
            productDtoList = productService.GetAllProduct(minPrice, maxPrice, sort);
        }

        // Thêm thông tin lọc vào model
        model.addAttribute("category", category);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("sort", sort);

        // Thêm danh sách sản phẩm vào model
        model.addAttribute("productDtoList", productDtoList);

        // Thêm thông tin phân trang
        int totalProducts = productDtoList.size();
        int productsPerPage = 12;
        int totalPages = (int) Math.ceil((double) totalProducts / productsPerPage);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalProducts", totalProducts);

        // Tính toán sản phẩm hiển thị trên trang hiện tại
        int startIndex = (page - 1) * productsPerPage;
        int endIndex = Math.min(startIndex + productsPerPage, totalProducts);

        if (startIndex < totalProducts) {
            model.addAttribute("productDtoList", productDtoList.subList(startIndex, endIndex));
        }

        List<ProductDto> featuredProducts = productService.getFeaturedProducts(4);
        model.addAttribute("featuredProducts", featuredProducts);

        return "san-pham";
    }

    @GetMapping("/product/{id}")
    public String getProductById(@PathVariable Long id, Model model) {
        ProductDto productDto = productService.getProductById(id);
        var reviewProduct = productDto.getReviews().stream()
                .filter(x -> x.getProduct().getProductID().equals(id))
                .sorted(Comparator.comparing(ReviewProduct::getDate).reversed()) // sắp xếp theo ngày
                .toList(); //
        productDto.setReviews(reviewProduct);
        // Lấy sản phẩm theo id từ service
        model.addAttribute("product", productDto); // Truyền sản phẩm vào model để Thymeleaf sử dụng
        //model.addAttribute("reviewCount", reviewProductService.countByProductId(id));

        if (productDto.getCategoryId() != null) {
            List<ProductDto> relatedProducts = productService.getRelatedProducts(productDto.getProductID(), productDto.getCategoryId(), 4);
            model.addAttribute("relatedProducts", relatedProducts);
        } else {
            // Nếu không có categoryId, có thể lấy sản phẩm nổi bật ngẫu nhiên hoặc bỏ qua
            model.addAttribute("relatedProducts", new ArrayList<>());
        }

        return "san-pham-chi-tiet";
    }

    // Phương thức thêm đánh giá
    @PostMapping("/product/{productId}/add-review")
    public String addReview(@PathVariable Long productId, @ModelAttribute ReviewProduct reviewProduct, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/LoginUser"; // Nếu chưa đăng nhập, chuyển hướng đến trang đăng nhập
        }

        Product product = productService.findById(productId);
        reviewProduct.setUser(user);
        reviewProduct.setProduct(product);
        reviewProduct.setDate(new Date());
        ReviewProduct isCheck = reviewProduct;// Gắn sản phẩm vào đánh giá
        reviewProductService.save(reviewProduct);

        return "redirect:/product/{productId}"; // Sau khi thêm đánh giá, quay lại trang sản phẩm
    }
}