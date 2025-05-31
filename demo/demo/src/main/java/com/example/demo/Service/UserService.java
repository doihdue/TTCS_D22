package com.example.demo.Service;

import com.example.demo.Model.Role;
import com.example.demo.Model.User;
import com.example.demo.Repository.RoleRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.Dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    public UserService(UserRepository _userRepository,RoleRepository _roleRepository) {
        userRepository = _userRepository;
        roleRepository = _roleRepository;
    }

    public List<User> GetAllUsers() {
        return userRepository.findAll();
    }

    public User SighUpUser(UserDto user1) {
        User user = new User();

        if("admin".equals(user1.getEmail())){
            var role = roleRepository.findByName("admin").orElseThrow(() -> new RuntimeException("Role admin not found"));
            user.setRole(role);
        } else {
            var role = roleRepository.findByName("user").orElseThrow(() -> new RuntimeException("Role user not found"));
            user.setRole(role);
        }
        // --- KẾT THÚC PHẦN LOGIC VAI TRÒ ---

        user.setEmail(user1.getEmail());
        //  MÃ HÓA MẬT KHẨU ***
        user.setPassword(passwordEncoder.encode(user1.getPassword()));
        user.setPhone(user1.getPhone());
        user.setLname(user1.getLname());
        user.setFname(user1.getFname());

        return userRepository.save(user);
    }

    public User LoginUser(UserDto user1) {

        Optional<User> userOptional = userRepository.findByEmail(user1.getEmail());
        if (userOptional.isPresent()) {
            User userInDb = userOptional.get();
            // SO SÁNH MẬT KHẨU ĐÃ BĂM ***
            if (passwordEncoder.matches(user1.getPassword(), userInDb.getPassword())) {
                return userInDb;
            }
        }
        return null; // Trả về null nếu không tìm thấy user hoặc mật khẩu không khớp

    }

    public void updateUser(User user) {
        // Phương thức này hiện tại chỉ lưu user.
        userRepository.save(user);
    }

    public boolean checkCurrentPassword(User user, String currentPassword) {
        // KIỂM TRA MẬT KHẨU ĐÃ BĂM ***
        return passwordEncoder.matches(currentPassword, user.getPassword());
    }

    public boolean resetPassword(String token, String newPassword) {
        Optional<User> userOpt = userRepository.findByResetToken(token);
        if (userOpt.isEmpty()) return false;

        User user = userOpt.get();
        // MÃ HÓA MẬT KHẨU MỚI ***
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null); // Xóa token sau khi dùng
        userRepository.save(user);
        return true;
    }

    // Thêm phương thức này để UserController có thể sử dụng để mã hóa mật khẩu
    // khi người dùng tự đổi mật khẩu của họ.
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}