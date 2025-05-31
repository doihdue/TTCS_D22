package com.example.demo.controller;

import com.example.demo.Model.Address;
import com.example.demo.Model.User;
import com.example.demo.Service.AddressService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/")
public class AddressController {
    @Autowired
    private AddressService addressService;

    // Thêm địa chỉ
    @PostMapping("/add-address")
    public String addAddress(@RequestParam String addressName,
                             @RequestParam String recipientName,
                             @RequestParam String recipientPhone,
                             @RequestParam String streetAddress,
                             @RequestParam(defaultValue = "false") boolean defaultAddress,
                             HttpSession session) {

        // Lấy người dùng hiện tại từ session (dùng với Spring Security)
        User user = (User) session.getAttribute("user");

        Address newAddress = Address.builder()
                .name(addressName)
                .diachi(streetAddress)
                .sdt(recipientPhone)
                .isDefault(defaultAddress)
                .user(user)
                .build();
        List<Address> addresses = addressService.findAllAddressesByUser(user);
        if (defaultAddress) {
            for (Address address : addresses) {
                address.setDefault(false);
                addressService.saveAddress(address);
            }

        }

        addressService.saveAddress(newAddress);

        return "redirect:/update-profile#addresses"; // Redirect đến trang danh sách địa chỉ
    }

    // Xóa địa chỉ

    @PostMapping("/delete-address/{id}")
    public String deleteAddress(@PathVariable("id") Long id) {
        addressService.deleteAddress(id);
        return "redirect:/update-profile#addresses"; // quay lại danh sách
    }

    @PostMapping("/set-default-address/{id}")
    public String setDefaultAddress(@PathVariable("id") Long id, HttpSession session) {
        // Lấy người dùng từ session
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login"; // Nếu người dùng chưa đăng nhập
        }

        // Lấy tất cả địa chỉ của người dùng
        List<Address> addresses = addressService.findAllAddressesByUser(user);

        // Cập nhật địa chỉ đang mặc định
        for (Address address : addresses) {
            if (address.isDefault()) {
                address.setDefault(false); // Đặt lại mặc định cho địa chỉ hiện tại
                addressService.saveAddress(address); // Lưu lại thay đổi
            }
        }

        // Cập nhật địa chỉ mới là mặc định
        Address defaultAddress = addressService.findAddressById(id);
        if (defaultAddress != null) {
            defaultAddress.setDefault(true); // Đặt làm mặc định
            addressService.saveAddress(defaultAddress); // Lưu lại thay đổi
        }

        return "redirect:/update-profile#addresses"; // Trả về trang danh sách địa chỉ
    }


}
