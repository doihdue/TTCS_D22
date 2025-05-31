package com.example.demo.Model;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "user")
@EqualsAndHashCode(exclude = "user")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<OrderDetail> orderDetails = new ArrayList<>();



    @OneToOne
    @JoinColumn(name = "user_id")  // This is the foreign key column
    private User user;
}
