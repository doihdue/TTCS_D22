package com.example.demo.controller;

import com.example.demo.Model.Address;
import com.example.demo.Model.Checkout;
import com.example.demo.Model.User;
import com.example.demo.Repository.CheckoutRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.AddressService;
import com.example.demo.Service.Dto.UserDto;
import com.example.demo.Service.EmailService;
import com.example.demo.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;


    @Autowired
    private CheckoutRepository checkoutRepository;
    private UserService userservice;
    private AddressService addressService;

    public UserController(UserService _userservice,AddressService _addressservice) {
        userservice = _userservice;
        addressService = _addressservice;
    }

    @RequestMapping("/signupUser")
    public String showSignupForm(Model model) {
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "signup";
    }

    @RequestMapping("/forgotPassword")
    public String forgotPassword(Model model) {
        UserDto user = new UserDto(); // Giữ nguyên nếu bạn muốn form có sẵn UserDto
        model.addAttribute("user", user);
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            model.addAttribute("message", "Email không tồn tại!");
            return "forgot-password";
        }

        User user = userOpt.get();
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.save(user); // Lưu user với token

        String resetLink = "http://localhost:8080/reset-password?token=" + token;
        // Gửi mail
        emailService.sendResetPasswordEmail(user.getEmail(), resetLink); // Bỏ comment khi EmailService hoạt động
        model.addAttribute("message", "Link đặt lại mật khẩu đã được gửi đến email."); // Thêm link để test nếu email chưa chạy
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetForm(@RequestParam("token") String token, Model model) {

        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(
            @RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("message", "Mật khẩu không khớp.");
            model.addAttribute("token", token); // Giữ lại token cho form
            return "reset-password";
        }
        // userservice.resetPassword sẽ mã hóa mật khẩu mới
        boolean success = userservice.resetPassword(token, password);
        if (success == false) {
            model.addAttribute("message", "Token không hợp lệ hoặc đã hết hạn.");
            return "reset-password"; // Trả về cùng trang để hiển thị message
        }
        return "redirect:/LoginUser";
    }

    @PostMapping("/signupUser")
    public String sighupUser(UserDto user){ // userservice.SighUpUser sẽ mã hóa mật khẩu
        userservice.SighUpUser(user);
        return "redirect:/LoginUser";
    }

    @PostMapping("/LoginUser")
    public String loginUser(UserDto userDto, HttpSession session, Model model) { // Đổi tên user thành userDto
        // userservice.LoginUser sẽ sử dụng passwordEncoder.matches()
        User result = userservice.LoginUser(userDto);

        if (result != null) {
            session.setAttribute("user", result);
            // var check = session.getAttribute("user"); // Dòng debug
            // Xử lý vai trò cẩn thận hơn để tránh NullPointerException
            String roleName = "USER"; // Mặc định
            if (result.getRole() != null && result.getRole().getName() != null) {
                roleName = result.getRole().getName();
            }
            String url = "admin".equalsIgnoreCase(roleName) ? "/admin/dashboard" : "/"; // So sánh không phân biệt hoa thường
            return "redirect:" + url;
        } else {
            model.addAttribute("error", "Email hoặc mật khẩu không đúng! Vui lòng thử lại.");
            model.addAttribute("user", userDto);
            return "login";
        }
    }


    @RequestMapping("/LoginUser")
    public String showloginUser(Model model) {
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "login";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/"; // Hoặc /LoginUser tùy bạn muốn
    }

    @GetMapping("/update-profile")
    public String showProfileForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/LoginUser";
        }
        // Lấy user mới nhất từ DB để đảm bảo dữ liệu chính xác
        User freshUser = userRepository.findById(user.getId()).orElse(null);
        if (freshUser == null) { // Nếu user không còn tồn tại trong DB
            session.invalidate();
            return "redirect:/LoginUser";
        }
        session.setAttribute("user", freshUser); // Cập nhật user trong session

        List<Address> addresses = addressService.findAllAddressesByUser(freshUser);
        model.addAttribute("addresses", addresses);
        List<Checkout> orders = checkoutRepository.findByUser(freshUser);
        model.addAttribute("orders", orders);
        model.addAttribute("user", freshUser); // Truyền User object
        return "thong-tin-tai-khoan";
    }

    @PostMapping("/update-profile")
    public String updateProfile(UserDto userDto, HttpSession session) {
        User userFromSession = (User) session.getAttribute("user");
        if (userFromSession == null) {
            // Nên là redirect để URL trên trình duyệt được cập nhật đúng
            return "redirect:/LoginUser";
        }
        // Lấy user từ DB để cập nhật, đảm bảo tính nhất quán
        User userToUpdate = userRepository.findById(userFromSession.getId()).orElse(null);
        if (userToUpdate == null) {
            session.invalidate();
            return "redirect:/LoginUser";
        }

        userToUpdate.setFname(userDto.getFname());
        userToUpdate.setLname(userDto.getLname());
        userToUpdate.setEmail(userDto.getEmail()); // Cân nhắc kiểm tra email trùng lặp nếu cho phép đổi
        userToUpdate.setPhone(userDto.getPhone());

        userservice.updateUser(userToUpdate);
        session.setAttribute("user", userToUpdate); // Cập nhật lại user trong session

        // Nên redirect về trang profile để thấy thay đổi và tránh submit lại form
        // return "thong-tin-tai-khoan";
        return "redirect:/update-profile";
    }

    @PostMapping("/change-password")
    public String updatePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session) { // Giữ nguyên các tham số bạn có

        User userFromSession = (User) session.getAttribute("user");
        if (userFromSession == null) {
            return "redirect:/LoginUser"; // Sửa "LoginUser" thành "redirect:/LoginUser"
        }
        // Lấy user mới nhất từ DB
        User userToUpdate = userRepository.findById(userFromSession.getId()).orElse(null);
        if (userToUpdate == null) {
            session.invalidate();
            return "redirect:/LoginUser";
        }


        if (!newPassword.equals(confirmPassword)) {

            // model.addAttribute("password_error_match", "Mật khẩu mới và xác nhận không khớp.");
            // return "thong-tin-tai-khoan"; // Hoặc trang chứa form đổi mật khẩu

            return "redirect:/change-password?error=true&reason=match"; // Thêm redirect
        }

        // *** SỬ DỤNG UserService ĐỂ KIỂM TRA MẬT KHẨU HIỆN TẠI ***
        if (!userservice.checkCurrentPassword(userToUpdate, currentPassword)) {
            // model.addAttribute("password_error_current", "Mật khẩu hiện tại không đúng.");
            // return "thong-tin-tai-khoan";
            return "redirect:/change-password?error=true&reason=currentInvalid"; // Thêm redirect
        }

        // *** MÃ HÓA MẬT KHẨU MỚI TRƯỚC KHI LƯU ***
        userToUpdate.setPassword(userservice.encodePassword(newPassword)); // Gọi phương thức encodePassword từ UserService
        userservice.updateUser(userToUpdate); // Lưu người dùng với mật khẩu đã được cập nhật và mã hóa

        session.setAttribute("user", userToUpdate); // Cập nhật lại user trong session

        // model.addAttribute("password_success", "Đổi mật khẩu thành công!");
        // return "thong-tin-tai-khoan";
        // Nên redirect để tránh resubmit form và URL được đẹp hơn
        // Nếu bạn có RedirectAttributes, có thể thêm thông báo thành công:
        // redirectAttributes.addFlashAttribute("password_success", "Đổi mật khẩu thành công!");
        return "redirect:/update-profile"; // Chuyển hướng về trang profile
    }
}