package com.example.demo.controller;

import com.example.demo.Model.Cart;
import com.example.demo.Model.OrderDetail;
import com.example.demo.Model.Product;
import com.example.demo.Model.User;
import com.example.demo.Repository.CartRepository;
import com.example.demo.Repository.OrderDetailRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.hibernate.query.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class CartController {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/giohang")
    public String viewCart(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/LoginUser";
        }

        Cart cart = cartRepository.findByUserId(user.getId());
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
        }

        // Đảm bảo orderDetails không null
        if (cart.getOrderDetails() == null) {
            cart.setOrderDetails(new ArrayList<>());
        }

        model.addAttribute("cart", cart);
        model.addAttribute("orderDetails", cart.getOrderDetails());

        // Calculate total price
        double totalPrice = 0;
        if (cart.getOrderDetails() != null) {
            for (OrderDetail orderDetail : cart.getOrderDetails()) {
                totalPrice += orderDetail.getTotalPrice();
            }
        }
        model.addAttribute("totalPrice", totalPrice);

        return "giohang";
    }

    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam("quantity") int quantity,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/LoginUser";
        }

        // Find or create cart for user
        Cart cart = cartRepository.findByUserId(user.getId());
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
        }

        // Đảm bảo orderDetails không null
        if (cart.getOrderDetails() == null) {
            cart.setOrderDetails(new ArrayList<>());
        }

        // Find product
        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Sản phẩm không tồn tại");
            return "redirect:/product/" + productId;
        }

        Product product = productOptional.get();

        // Tìm orderDetail có sản phẩm này
        OrderDetail existingOrderDetail = null;
        for (OrderDetail od : cart.getOrderDetails()) {
            // Kiểm tra xem orderDetail này có chứa sản phẩm cần tìm không
            for (Product p : od.getProducts()) {
                if (p.getProductID().equals(productId)) {
                    existingOrderDetail = od;
                    break;
                }
            }
            if (existingOrderDetail != null) break;
        }

        if (existingOrderDetail != null) {
            // Cập nhật số lượng
            existingOrderDetail.setQuantity(existingOrderDetail.getQuantity() + quantity);
            orderDetailRepository.save(existingOrderDetail);
        } else {
            // Tạo orderDetail mới
            OrderDetail newOrderDetail = new OrderDetail();
            newOrderDetail.setCart(cart);
            newOrderDetail.setQuantity(quantity);
            newOrderDetail.setDate(new Date());
            newOrderDetail.setPrice(product.getPrice());
            newOrderDetail.setVoucher(product.getVoucherPrice());

            newOrderDetail.setCategoryName(product.getProductName());
            newOrderDetail.setImageUrl(product.getLinkImage());

            List<Product> products = new ArrayList<>();
            products.add(product);

            newOrderDetail.setProducts(products);

            // Lưu orderDetail trước để có ID
            orderDetailRepository.save(newOrderDetail);

            // Thêm sản phẩm vào orderDetail
            product.setOrderdetail(newOrderDetail);
            productRepository.save(product);

            // Thêm orderDetail vào cart
            cart.getOrderDetails().add(newOrderDetail);
            cartRepository.save(cart);
        }

        redirectAttributes.addFlashAttribute("success", "Đã thêm sản phẩm vào giỏ hàng");
        return "redirect:/giohang";
    }

    @PostMapping("/update-cart")
    public String updateCart(@RequestParam("orderDetailId") Long orderDetailId,
                             @RequestParam("quantity") int quantity,
                             RedirectAttributes redirectAttributes) {

        if (quantity <= 0) {
            orderDetailRepository.deleteById(orderDetailId);
            redirectAttributes.addFlashAttribute("success", "Đã xóa sản phẩm khỏi giỏ hàng");
        } else {
            Optional<OrderDetail> orderDetailOptional = orderDetailRepository.findById(orderDetailId);
            if (orderDetailOptional.isPresent()) {
                OrderDetail orderDetail = orderDetailOptional.get();
                orderDetail.setQuantity(quantity);
                orderDetailRepository.save(orderDetail);
                redirectAttributes.addFlashAttribute("success", "Đã cập nhật số lượng sản phẩm");
            }
        }

        return "redirect:/giohang";
    }

    @PostMapping("/remove-from-cart")
    public String removeFromCart(@RequestParam("orderDetailId") Long orderDetailId,
                                 RedirectAttributes redirectAttributes) {

        OrderDetail od = orderDetailRepository.findById(orderDetailId).orElseThrow();

        for (Product p : od.getProduct()) {
            p.setOrderdetail(null);
            productRepository.save(p);
        }
        orderDetailRepository.deleteById(orderDetailId);
        redirectAttributes.addFlashAttribute("success", "Đã xóa sản phẩm khỏi giỏ hàng");

        return "redirect:/giohang";
    }
}