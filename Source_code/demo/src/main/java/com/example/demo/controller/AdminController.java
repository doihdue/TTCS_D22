package com.example.demo.controller;


import com.example.demo.Model.Address;
import com.example.demo.Model.Checkout;
import com.example.demo.Model.Role;
import com.example.demo.Model.User;
import com.example.demo.Repository.AddressRepository;
import com.example.demo.Repository.CheckoutRepository;
import com.example.demo.Repository.RoleRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.Dto.ProductDto;
import com.example.demo.Service.EmailService;
import com.example.demo.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class AdminController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CheckoutRepository checkoutRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private EmailService emailService;

    @GetMapping("/admin/dashboard")
    public String showAdminDashboard(Model model) {
        // 1. Thống kê sản phẩm
        long totalProducts = productService.countTotalProducts();
        long outOfStockProducts = productService.countOutOfStockProducts();
        long discountedProducts = productService.countDiscountedProducts();

        // Gửi các giá trị thống kê vào model
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("outOfStockProducts", outOfStockProducts);
        model.addAttribute("discountedProducts", discountedProducts);

        // 2. Thống kê đơn hàng
        List<Checkout> allOrdersForStats = checkoutRepository.findAll(); // Lấy tất cả đơn hàng
        long totalOrders = allOrdersForStats.size();
        long processingOrdersDashboard = allOrdersForStats.stream().filter(o -> "Đang xử lý".equals(o.getOrderStatus())).count();
        long pendingConfirmationOrders = allOrdersForStats.stream().filter(o -> "Đã xác nhận".equals(o.getOrderStatus())).count();
        long shippingOrdersDashboard = allOrdersForStats.stream().filter(o -> "Đang giao hàng".equals(o.getOrderStatus())).count();
        long deliveredOrders = allOrdersForStats.stream().filter(o -> "Đã giao hàng".equals(o.getOrderStatus())).count();

        model.addAttribute("totalOrdersDashboard", totalOrders);
        model.addAttribute("processingOrdersDashboard", processingOrdersDashboard);
        model.addAttribute("pendingConfirmationOrders", pendingConfirmationOrders);
        model.addAttribute("shippingOrdersDashboard", shippingOrdersDashboard);
        model.addAttribute("deliveredOrders", deliveredOrders);

        // 3. Tính doanh thu (ví dụ: 30 ngày qua)
        Date thirtyDaysAgo = new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000);
        double revenueLast30Days = allOrdersForStats.stream()
                .filter(o -> ("Đã giao hàng".equals(o.getOrderStatus()) || "Đã thanh toán".equals(o.getPaymentStatus())) &&
                        o.getOrderDate() != null && o.getOrderDate().after(thirtyDaysAgo))
                .mapToDouble(Checkout::getOrderTotal)
                .sum();
        model.addAttribute("revenueLast30Days", revenueLast30Days);

        // 4. Thống kê khách hàng
        long totalCustomers = userRepository.count();
        // Khách hàng mới trong 7 ngày
        Date sevenDaysAgo = new Date(System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000);
        long newCustomersLast7Days = userRepository.findAll().stream() // userRepository.countByRegistrationDateAfter(sevenDaysAgo);
                .filter(c -> c.getRegistrationDate() != null && c.getRegistrationDate().after(sevenDaysAgo))
                .count();
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("newCustomersLast7Days", newCustomersLast7Days);

        // 5. (Tùy chọn) Lấy một vài đơn hàng mới nhất để hiển thị
        Pageable latestOrdersPageable = PageRequest.of(0, 5, Sort.by("orderDate").descending()); // 5 đơn hàng mới nhất
        List<Checkout> latestOrders = checkoutRepository.findAll(latestOrdersPageable).getContent();
        model.addAttribute("latestOrders", latestOrders);

        // 6. (Tùy chọn) Lấy một vài sản phẩm bán chạy hoặc mới thêm
        // Cái này sẽ cần logic phức tạp hơn, ví dụ dựa trên số lượng bán trong OrderItem
        // Hoặc sản phẩm mới thêm:
        Pageable latestProductsPageable = PageRequest.of(0, 5, Sort.by("productID").descending()); // 5 sản phẩm mới nhất theo ID
        List<ProductDto> latestProducts = productService.GetAllProducts(latestProductsPageable);
        model.addAttribute("latestProducts", latestProducts);


        return "/admin/admin-dashboard";
    }

    @GetMapping("/add-product")
    public String addProduct(Model model) {
        ProductDto product = new ProductDto();
        model.addAttribute("product", product);
        return "/admin/add-products";
    }

    // Phương thức gộp thống kê vào model
    private void addStatsToModel(Model model) {
        long totalProducts = productService.countTotalProducts();
        long outOfStockProducts = productService.countOutOfStockProducts();
        long discountedProducts = productService.countDiscountedProducts();

        // Gửi các giá trị thống kê vào model
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("outOfStockProducts", outOfStockProducts);
        model.addAttribute("discountedProducts", discountedProducts);
    }

    @GetMapping("/admin/product-detail/{id}")
    public String viewProductDetail(@PathVariable("id") Long id, Model model) {
        ProductDto productDto = productService.getProductById(id); // Sử dụng lại phương thức đã có
        if (productDto == null) {
            // Xử lý trường hợp không tìm thấy sản phẩm, ví dụ chuyển hướng lỗi
            return "error/404"; // Tạo một trang lỗi 404 nếu cần
        }
        model.addAttribute("product", productDto);


        // List<ReviewProduct> reviews = reviewProductService.findReviewsByProductId(id); // Giả sử bạn có service này
        // model.addAttribute("reviews", reviews);

        return "/admin/admin-product-detail";
    }

    @PostMapping("/add-product")
    public String addProduct(@ModelAttribute("product") ProductDto productDTO, Model model) {
        // Gọi service để thêm sản phẩm vào cơ sở dữ liệu
        productService.addProduct(productDTO);

        // Gọi hàm gộp thống kê vào model
        addStatsToModel(model);

        // Sau khi thêm sản phẩm thành công, chuyển hướng đến trang danh sách sản phẩm
        return "redirect:/admin-product";
    }

    @GetMapping("/admin-product")
    public String getProduct(Model model) {
        List<ProductDto> productDtoList = productService.GetAllProduct();
        model.addAttribute("productDtoList", productDtoList);

        // Gọi hàm gộp thống kê vào model
        addStatsToModel(model);

        return "/admin/admin-products";
    }

    @GetMapping("/admin/edit-product/{id}")
    public String editProduct(@PathVariable("id") Long id, Model model) {
        ProductDto productDto = productService.getProductById(id);
        model.addAttribute("product", productDto);

        // Gọi hàm gộp thống kê vào model
        addStatsToModel(model);

        return "/admin/add-products"; // form thêm/sửa sản phẩm dùng chung
    }

    @PostMapping("/admin/delete-product/{id}")
    public String deleteProduct(@PathVariable("id") Long id, Model model) {
        productService.deleteProduct(id);

        // Gọi hàm gộp thống kê vào model
        addStatsToModel(model);

        return "redirect:/admin-product"; // quay lại danh sách
    }

    // Trang quản lý đơn hàng
    @GetMapping("/orders")
    public String showOrderManagement(Model model) {
        List<Checkout> orders = checkoutRepository.findAll();

        // Thống kê đơn hàng
        long totalOrders = orders.size();
        long processingOrders = orders.stream().filter(o -> "Đang xử lý".equals(o.getOrderStatus())).count();
        long shippingOrders = orders.stream().filter(o -> "Đang giao hàng".equals(o.getOrderStatus())).count();

        // Tính doanh thu tháng
        double monthlyRevenue = orders.stream()
                .filter(o -> {
                    // Lọc đơn hàng trong tháng hiện tại
                    Date now = new Date();
                    Date orderDate = o.getOrderDate();
                    if (orderDate == null) return false;
                    return orderDate.getMonth() == now.getMonth() && orderDate.getYear() == now.getYear();
                })
                .mapToDouble(Checkout::getOrderTotal)
                .sum();

        model.addAttribute("orders", orders);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("processingOrders", processingOrders);
        model.addAttribute("shippingOrders", shippingOrders);
        model.addAttribute("monthlyRevenue", String.format("%.1fM", monthlyRevenue / 1000000));

        return "/admin/order-management";
    }

    // Chi tiết đơn hàng
    @GetMapping("/order-detail/{id}")
    public String showOrderDetail(@PathVariable("id") Long id, Model model) {
        Optional<Checkout> orderOpt = checkoutRepository.findById(id);
        if (orderOpt.isPresent()) {
            model.addAttribute("order", orderOpt.get());
            return "/admin/order-detail";
        }
        return "redirect:/orders";
    }

    // Cập nhật trạng thái đơn hàng
    @PostMapping("/update-order-status")
    public String updateOrderStatus(@RequestParam("orderId") Long orderId,
                                    @RequestParam("orderStatus") String orderStatus,
                                    @RequestParam(value = "statusNote", required = false) String statusNote,
                                    RedirectAttributes redirectAttributes) {
        Optional<Checkout> orderOpt = checkoutRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Checkout order = orderOpt.get();
            order.setOrderStatus(orderStatus);

            // Nếu đơn hàng đã giao hàng và thanh toán COD, cập nhật trạng thái thanh toán
            if ("Đã giao hàng".equals(orderStatus) && "COD".equals(order.getPaymentMethod().name()) && "Chưa thanh toán".equals(order.getPaymentStatus())) {
                order.setPaymentStatus("Đã thanh toán");
                order.setPaymentDate(new Date());
            }

            // Thêm ghi chú nếu có
            if (statusNote != null && !statusNote.isEmpty()) {
                String currentNotes = order.getNotes() != null ? order.getNotes() : "";
                order.setNotes(currentNotes + "\n[" + new Date() + "] Cập nhật trạng thái: " + statusNote);
            }

            checkoutRepository.save(order);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái đơn hàng thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng!");
        }
        return "redirect:/orders";
    }

    // Cập nhật trạng thái thanh toán
    @PostMapping("/update-payment-status")
    public String updatePaymentStatus(@RequestParam("orderId") Long orderId,
                                      @RequestParam("paymentStatus") String paymentStatus,
                                      @RequestParam(value = "transactionId", required = false) String transactionId,
                                      @RequestParam(value = "paymentNote", required = false) String paymentNote,
                                      RedirectAttributes redirectAttributes) {
        Optional<Checkout> orderOpt = checkoutRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Checkout order = orderOpt.get();
            order.setPaymentStatus(paymentStatus);

            if ("Đã thanh toán".equals(paymentStatus)) {
                order.setPaymentDate(new Date());
            }

            // Thêm thông tin giao dịch và ghi chú
            StringBuilder noteBuilder = new StringBuilder();
            if (order.getNotes() != null && !order.getNotes().isEmpty()) {
                noteBuilder.append(order.getNotes()).append("\n");
            }

            noteBuilder.append("[").append(new Date()).append("] Cập nhật thanh toán: ").append(paymentStatus);

            if (transactionId != null && !transactionId.isEmpty()) {
                noteBuilder.append(" - Mã giao dịch: ").append(transactionId);
            }

            if (paymentNote != null && !paymentNote.isEmpty()) {
                noteBuilder.append(" - ").append(paymentNote);
            }

            order.setNotes(noteBuilder.toString());
            checkoutRepository.save(order);

            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái thanh toán thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng!");
        }
        return "redirect:/orders";
    }

    // Hủy đơn hàng
    @PostMapping("/cancel-order")
    public String cancelOrder(@RequestParam("orderId") Long orderId,
                              @RequestParam("cancelReason") String cancelReason,
                              @RequestParam(value = "otherReason", required = false) String otherReason,
                              RedirectAttributes redirectAttributes) {
        Optional<Checkout> orderOpt = checkoutRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Checkout order = orderOpt.get();
            order.setOrderStatus("Đã hủy");

            // Xác định lý do hủy
            String reason = cancelReason;
            if ("Khác".equals(cancelReason) && otherReason != null && !otherReason.isEmpty()) {
                reason = otherReason;
            }

            // Thêm ghi chú
            String currentNotes = order.getNotes() != null ? order.getNotes() : "";
            order.setNotes(currentNotes + "\n[" + new Date() + "] Đơn hàng đã bị hủy. Lý do: " + reason);

            checkoutRepository.save(order);
            redirectAttributes.addFlashAttribute("success", "Đơn hàng đã được hủy thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng!");
        }
        return "redirect:/orders";
    }

    // Trang quản lý khách hàng
    @GetMapping("/customers")
    public String showCustomerManagement(Model model) {
        Role userRole = roleRepository.findByName("user")
                .orElseThrow(() -> new RuntimeException("Role 'user' not found. Please ensure it exists in the database."));

        // 2. Lấy danh sách khách hàng (User) chỉ có vai trò là "user"
        List<User> customers = userRepository.findByRole(userRole);

        // Thống kê khách hàng
        long totalCustomers = customers.size();

        // Khách hàng mới trong 30 ngày
        Date thirtyDaysAgo = new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000);
        long newCustomers = customers.stream()
                .filter(c -> c.getRegistrationDate() != null && c.getRegistrationDate().after(thirtyDaysAgo))
                .count();

        // Khách hàng thân thiết (có từ 3 đơn hàng trở lên)
        long loyalCustomers = customers.stream()
                .filter(c -> c.getOrderCount() >= 3)
                .count();

        // Giá trị trung bình đơn hàng
        double avgOrderValue = customers.stream()
                .filter(c -> c.getOrderCount() > 0)
                .mapToDouble(c -> c.getTotalSpent() / c.getOrderCount())
                .average()
                .orElse(0);

        model.addAttribute("customers", customers);
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("newCustomers", newCustomers);
        model.addAttribute("loyalCustomers", loyalCustomers);
        model.addAttribute("averageOrderValue", String.format("%.0fK", avgOrderValue / 1000));

        return "/admin/customer-management";
    }

    // Chi tiết khách hàng
    @GetMapping("/customer-detail/{id}")
    public String showCustomerDetail(@PathVariable("id") Long id, Model model) {
        Optional<User> customerOpt = userRepository.findById(id);
        if (customerOpt.isPresent()) {
            User customer = customerOpt.get();
            List<Checkout> customerOrders = checkoutRepository.findByUserId(id);

            // Tìm địa chỉ mặc định một cách an toàn
            Optional<Address> defaultAddressOpt = customer.getAddresses().stream()
                    .filter(x -> x.isDefault() == true)
                    .findFirst();
            customer.setOrderCount(customerOrders.size());
            double totalSpent = customerOrders.stream()
                    .filter(order -> "Đã thanh toán".equals(order.getPaymentStatus()))
                    .mapToDouble(Checkout::getOrderTotal) // hoặc order -> order.getTotalAmount()
                    .sum();

            customer.setTotalSpent(totalSpent);

            model.addAttribute("customer", customer);
            model.addAttribute("customerOrders", customerOrders);

            if (defaultAddressOpt.isPresent()) {
                Address address = defaultAddressOpt.get();
                model.addAttribute("addresses", address.getDiachi());
            } else {
                // Xử lý trường hợp không có địa chỉ mặc định
                model.addAttribute("addresses", "Không có địa chỉ mặc định");

            }
            return "/admin/customer-detail";
        }
        return "redirect:/customers";
    }

    @GetMapping("/customer-orders/{id}")
    public String showCustomerOrders(@PathVariable("id") Long id, Model model) {
        Optional<User> customerOpt = userRepository.findById(id);
        if (customerOpt.isPresent()) {
            User customer = customerOpt.get();
            List<Checkout> customerOrders = checkoutRepository.findByUserId(id);

            double totalSpent = customerOrders.stream()
                    .filter(o -> o.getOrderStatus().equals("Đã giao hàng") && o.getPaymentStatus().equals("Đã thanh toán"))
                    .mapToDouble(Checkout::getOrderTotal)
                    .sum();
            // Tính toán số lượng đơn hàng theo trạng thái
            long processingCount = customerOrders.stream()
                    .filter(o -> o.getOrderStatus().equals("Đang xử lý") ||
                            o.getOrderStatus().equals("Đã xác nhận") ||
                            o.getOrderStatus().equals("Đang giao hàng"))
                    .count();

            long completedCount = customerOrders.stream()
                    .filter(o -> o.getOrderStatus().equals("Đã giao hàng"))
                    .count();

            // Thêm các giá trị đếm vào model
            model.addAttribute("customer", customer);
            model.addAttribute("customerOrders", customerOrders);
            model.addAttribute("processingCount", processingCount);
            model.addAttribute("completedCount", completedCount);
            model.addAttribute("totalSpent", totalSpent);

            return "/admin/customer-orders";
        }
        return "redirect:/customers";
    }


    // Gửi email cho khách hàng
    @PostMapping("/send-email")
    public String sendEmail(@RequestParam("customerId") Long customerId,
                            @RequestParam("subject") String subject,
                            @RequestParam("content") String content,
                            RedirectAttributes redirectAttributes) {
        Optional<User> customerOpt = userRepository.findById(customerId);
        if (customerOpt.isPresent()) {
            User customer = customerOpt.get();
            emailService.SendCustomerEmail(customer.getEmail(), subject , content);
            // Bỏ comment khi EmailService hoạt động
            redirectAttributes.addFlashAttribute("success", "Email đã được gửi thành công đến " + customer.getEmail());
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách hàng!");
        }
        return "redirect:/customers";
    }

    // Vô hiệu hóa tài khoản khách hàng
    @PostMapping("/deactivate-customer")
    public String deactivateCustomer(@RequestParam("customerId") Long customerId,
                                     @RequestParam(value = "reason", required = false) String reason,
                                     RedirectAttributes redirectAttributes) {
        Optional<User> customerOpt = userRepository.findById(customerId);
        if (customerOpt.isPresent()) {
            User customer = customerOpt.get();
            customer.setActive(false);

            if (reason != null && !reason.isEmpty()) {

            }

            userRepository.save(customer);
            redirectAttributes.addFlashAttribute("success", "Tài khoản khách hàng đã được vô hiệu hóa thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách hàng!");
        }
        return "redirect:/customers";
    }
}

