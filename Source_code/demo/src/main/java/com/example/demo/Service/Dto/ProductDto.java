package com.example.demo.Service.Dto;

import com.example.demo.Model.Product;
import com.example.demo.Model.ReviewProduct;

import java.util.List;

public class ProductDto {
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
    private Long categoryId;
    private String categoryName;// Để lấy category từ form
    private List<ReviewProduct> reviews; // Thêm trường reviews để lưu thông tin đánh giá

    // Getters và Setters

    public List<ReviewProduct> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewProduct> reviews) {
        this.reviews = reviews;
    }

    // Getters và Setters

    private Long productID; // Đã có trong Product entity nhưng cần đưa vào ProductDto

    public Long getProductID() {
        return productID;
    }

    public void setProductID(Long productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getLinkImage() {
        return linkImage;
    }

    public void setLinkImage(String linkImage) {
        this.linkImage = linkImage;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public double getVoucherPrice() {
        return voucherPrice;
    }

    public void setVoucherPrice(double voucherPrice) {
        this.voucherPrice = voucherPrice;
    }

    public String getDecriptionShort() {
        return decriptionShort;
    }

    public void setDecriptionShort(String decriptionShort) {
        this.decriptionShort = decriptionShort;
    }

    public String getXuatXu() {
        return xuatXu;
    }

    public void setXuatXu(String xuatXu) {
        this.xuatXu = xuatXu;
    }

    public float getDungtich() {
        return dungtich;
    }

    public void setDungtich(float dungtich) {
        this.dungtich = dungtich;
    }

    public String getHanSudung() {
        return hanSudung;
    }

    public void setHanSudung(String hanSudung) {
        this.hanSudung = hanSudung;
    }

    public String getBaoquan() {
        return baoquan;
    }

    public void setBaoquan(String baoquan) {
        this.baoquan = baoquan;
    }

    public String getDesciptionDetail() {
        return desciptionDetail;
    }

    public void setDesciptionDetail(String desciptionDetail) {
        this.desciptionDetail = desciptionDetail;
    }

    public String getBenefitProduct() {
        return benefitProduct;
    }

    public void setBenefitProduct(String benefitProduct) {
        this.benefitProduct = benefitProduct;
    }

    public String getThongtindinhduong() {
        return thongtindinhduong;
    }

    public void setThongtindinhduong(String thongtindinhduong) {
        this.thongtindinhduong = thongtindinhduong;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
