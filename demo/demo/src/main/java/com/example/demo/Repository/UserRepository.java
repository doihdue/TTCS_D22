package com.example.demo.Repository;

import com.example.demo.Model.Role;
import com.example.demo.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String username);

    List<User> findByRole(Role role);

    Optional<User> findByResetToken(String token);
}
