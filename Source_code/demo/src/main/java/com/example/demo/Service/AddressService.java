package com.example.demo.Service;

import com.example.demo.Model.Address;
import com.example.demo.Model.User;
import com.example.demo.Repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;

    // Lấy tất cả các địa chỉ
    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    public List<Address> findAllAddressesByUser(User user) {
        return addressRepository.findByUser(user);
    }

    // Lưu địa chỉ vào cơ sở dữ liệu
    public void saveAddress(Address address) {
        addressRepository.save(address);
    }

    public void setDefaultAddress(User user, Long addressId) {
        // Đặt địa chỉ mới làm mặc định và bỏ đánh dấu mặc định với các địa chỉ còn lại
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new RuntimeException("Address not found"));
        address.setDefault(true);
        addressRepository.save(address);

        // Cập nhật các địa chỉ còn lại để không còn là mặc định
        List<Address> allAddresses = addressRepository.findByUser(user);
        for (Address addr : allAddresses) {
            if (addr.getId() != addressId) {
                addr.setDefault(false);
                addressRepository.save(addr);
            }
        }
    }

    public void deleteAddress(Long addressId) {
        // Xóa địa chỉ
        addressRepository.deleteById(addressId);
    }

    public Address findAddressById(long id) {
        return addressRepository.findById(id).orElse(null);  // Tìm địa chỉ hoặc trả về null nếu không tìm thấy
    }

}
