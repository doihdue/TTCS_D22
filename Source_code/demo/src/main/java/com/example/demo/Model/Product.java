package com.example.demo.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productID;

    private String productName;
    private String linkImage;
    private int stock;
    private double price;

    private double oldPrice;
    private double voucherPrice;
    private String decriptionShort;
    private String xuatXu;
    private float dungtich;
    private String hanSudung;
    private String baoquan;
    private String desciptionDetail;
    private String benefitProduct;
    private String thongtindinhduong;

    public List<ReviewProduct> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewProduct> reviews) {
        this.reviews = reviews;
    }

    // Nhiều sản phẩm thuộc 1 danh mục
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "orderdetail_id")  // This is the foreign key column
    private OrderDetail orderdetail;

    // sản phẩm có nhiều đánh giá
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewProduct> reviews;

}
