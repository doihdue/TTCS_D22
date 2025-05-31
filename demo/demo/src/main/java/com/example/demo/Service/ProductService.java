package com.example.demo.Service;

import com.example.demo.Model.Category;
import com.example.demo.Model.Product;
import com.example.demo.Model.ReviewProduct;
import com.example.demo.Repository.CategoryRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.ReviewProductRepository;
import com.example.demo.Service.Dto.ProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ReviewProductRepository reviewProductRepository;

    public Product findById(Long productId) {
        return productRepository.findById(productId).orElse(null);  // Trả về null nếu không tìm thấy sản phẩm
    }

    public void addProduct(ProductDto productDto) {
        // Kiểm tra xem Category có tồn tại không và lấy category
        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category with id " + productDto.getCategoryId() + " not found"));

        Product product;

        if (productDto.getProductID() != null) {
            // Nếu có ID -> đang sửa sản phẩm
            product = productRepository.findById(productDto.getProductID())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
        } else {
            // Nếu chưa có ID -> đang thêm mới sản phẩm
            product = new Product();
        }

        // Gán dữ liệu thủ công
        product.setProductName(productDto.getProductName());
        product.setLinkImage(productDto.getLinkImage());
        product.setStock(productDto.getStock());
        product.setPrice(productDto.getPrice());
        product.setOldPrice(productDto.getOldPrice());
        product.setVoucherPrice(productDto.getVoucherPrice());
        product.setDecriptionShort(productDto.getDecriptionShort());
        product.setXuatXu(productDto.getXuatXu());
        product.setDungtich(productDto.getDungtich());
        product.setHanSudung(productDto.getHanSudung());
        product.setBaoquan(productDto.getBaoquan());
        product.setDesciptionDetail(productDto.getDesciptionDetail());
        product.setBenefitProduct(productDto.getBenefitProduct());
        product.setThongtindinhduong(productDto.getThongtindinhduong());
        product.setCategory(category);

        // Lưu vào DB
        productRepository.save(product);
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<ReviewProduct> reviews = reviewProductRepository.findAll();

        ProductDto dto = new ProductDto();
        dto.setProductID(product.getProductID());
        dto.setProductName(product.getProductName());
        dto.setLinkImage(product.getLinkImage());
        dto.setStock(product.getStock());
        dto.setPrice(product.getPrice());
        dto.setOldPrice(product.getOldPrice());
        dto.setVoucherPrice(product.getVoucherPrice());
        dto.setDecriptionShort(product.getDecriptionShort());
        dto.setXuatXu(product.getXuatXu());
        dto.setDungtich(product.getDungtich());
        dto.setHanSudung(product.getHanSudung());
        dto.setBaoquan(product.getBaoquan());
        dto.setDesciptionDetail(product.getDesciptionDetail());
        dto.setBenefitProduct(product.getBenefitProduct());
        dto.setThongtindinhduong(product.getThongtindinhduong());
        dto.setReviews(reviews);
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }

        return dto;
    }

    private List<ProductDto> ConvertProduct(List<Product> listproduct){
        List<ProductDto> listProductDto = new ArrayList<>();

        for (Product product : listproduct) {
            ProductDto productDto = new ProductDto();
            productDto.setProductID(product.getProductID());
            productDto.setProductName(product.getProductName());
            productDto.setLinkImage(product.getLinkImage());
            productDto.setStock(product.getStock());
            productDto.setPrice(product.getPrice());
            productDto.setOldPrice(product.getOldPrice());
            productDto.setVoucherPrice(product.getVoucherPrice());
            productDto.setDecriptionShort(product.getDecriptionShort());
            productDto.setXuatXu(product.getXuatXu());
            productDto.setDungtich(product.getDungtich());
            productDto.setHanSudung(product.getHanSudung());
            productDto.setBaoquan(product.getBaoquan());
            productDto.setDesciptionDetail(product.getDesciptionDetail());
            productDto.setBenefitProduct(product.getBenefitProduct());

            List<ReviewProduct> reviews = reviewProductRepository.findAll();
            productDto.setReviews(reviews);

            // Lấy categoryId và truy vấn bảng Category để lấy tên
            if (product.getCategory() != null) {
                Long categoryId = product.getCategory().getId();
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new RuntimeException("Category not found"));
                productDto.setCategoryName(category.getName());  // Gán tên category vào ProductDto
            }

            listProductDto.add(productDto);
        }
        return listProductDto;
    }

    // Phương thức lấy danh sách sản phẩm, lấy tên category từ categoryId
    public List<ProductDto> GetAllProduct() {
        List<Product> listproduct = productRepository.findAll();
        return this.ConvertProduct(listproduct);
    }

    // Phương thức lấy danh sách sản phẩm với bộ lọc giá và sắp xếp
    public List<ProductDto> GetAllProduct(Double minPrice, Double maxPrice, String sort) {
        List<Product> listproduct = productRepository.findAll();

        // Lọc theo giá nếu có
        if (minPrice != null || maxPrice != null) {
            double min = minPrice != null ? minPrice : 0;
            double max = maxPrice != null ? maxPrice : Double.MAX_VALUE;

            listproduct = listproduct.stream()
                    .filter(p -> p.getPrice() >= min && p.getPrice() <= max)
                    .collect(Collectors.toList());
        }

        // Sắp xếp
        if (sort != null) {
            switch (sort) {
                case "price-asc":
                    listproduct.sort(Comparator.comparing(Product::getPrice));
                    break;
                case "price-desc":
                    listproduct.sort(Comparator.comparing(Product::getPrice).reversed());
                    break;
                case "name-asc":
                    listproduct.sort(Comparator.comparing(Product::getProductName));
                    break;
                case "name-desc":
                    listproduct.sort(Comparator.comparing(Product::getProductName).reversed());
                    break;
                default:
                    // Mặc định không sắp xếp
                    break;
            }
        }

        return this.ConvertProduct(listproduct);
    }

    // Phương thức lấy sản phẩm theo danh mục
    public List<ProductDto> getProductsByCategory(String category) {

        var listproduct = productRepository.findAll();
        var listProductByCategory = listproduct.stream()
                .filter(product -> product.getCategory() != null && product.getCategory().getName().equals(category))
                .collect(Collectors.toList());
        return this.ConvertProduct(listProductByCategory);
    }

    // Phương thức lấy sản phẩm theo danh mục với bộ lọc giá và sắp xếp
    public List<ProductDto> getProductsByCategory(String category, Double minPrice, Double maxPrice, String sort) {
        var listproduct = productRepository.findAll();
        var listProductByCategory = listproduct.stream()
                .filter(product -> product.getCategory() != null && product.getCategory().getName().equals(category))
                .collect(Collectors.toList());

        // Lọc theo giá nếu có
        if (minPrice != null || maxPrice != null) {
            double min = minPrice != null ? minPrice : 0;
            double max = maxPrice != null ? maxPrice : Double.MAX_VALUE;

            listProductByCategory = listProductByCategory.stream()
                    .filter(p -> p.getPrice() >= min && p.getPrice() <= max)
                    .collect(Collectors.toList());
        }

        // Sắp xếp
        if (sort != null) {
            switch (sort) {
                case "price-asc":
                    listProductByCategory.sort(Comparator.comparing(Product::getPrice));
                    break;
                case "price-desc":
                    listProductByCategory.sort(Comparator.comparing(Product::getPrice).reversed());
                    break;
                case "name-asc":
                    listProductByCategory.sort(Comparator.comparing(Product::getProductName));
                    break;
                case "name-desc":
                    listProductByCategory.sort(Comparator.comparing(Product::getProductName).reversed());
                    break;
                default:
                    // Mặc định không sắp xếp
                    break;
            }
        }

        return this.ConvertProduct(listProductByCategory);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // Đếm tổng số sản phẩm
    public long countTotalProducts() {
        return productRepository.count();
    }

    // Đếm số sản phẩm có số lượng dưới 5 (hết hàng)
    public long countOutOfStockProducts() {
        return productRepository.countByStockLessThan(5);
    }

    // Đếm số sản phẩm giảm giá (có giá cũ và giá mới)
    public long countDiscountedProducts() {
        return productRepository.countByOldPriceGreaterThan(0);
    }

    // Tìm kiếm cơ bản
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return productRepository.findAll();
        }
        return productRepository.searchProducts(keyword);
    }

    // Tìm kiếm sản phẩm với bộ lọc
    public List<ProductDto> searchProducts(String keyword, String category, Double minPrice, Double maxPrice, String sort) {
        List<Product> products;

        if (keyword == null || keyword.trim().isEmpty()) {
            products = productRepository.findAll();
        } else {
            products = productRepository.searchProducts(keyword);
        }

        // Lọc theo danh mục nếu có
        if (category != null && !category.trim().isEmpty()) {
            products = products.stream()
                    .filter(product -> product.getCategory() != null && product.getCategory().getName().equals(category))
                    .collect(Collectors.toList());
        }

        // Lọc theo giá nếu có
        if (minPrice != null || maxPrice != null) {
            double min = minPrice != null ? minPrice : 0;
            double max = maxPrice != null ? maxPrice : Double.MAX_VALUE;

            products = products.stream()
                    .filter(p -> p.getPrice() >= min && p.getPrice() <= max)
                    .collect(Collectors.toList());
        }

        // Sắp xếp
        if (sort != null) {
            switch (sort) {
                case "price-asc":
                    products.sort(Comparator.comparing(Product::getPrice));
                    break;
                case "price-desc":
                    products.sort(Comparator.comparing(Product::getPrice).reversed());
                    break;
                case "name-asc":
                    products.sort(Comparator.comparing(Product::getProductName));
                    break;
                case "name-desc":
                    products.sort(Comparator.comparing(Product::getProductName).reversed());
                    break;
                default:
                    // Mặc định không sắp xếp
                    break;
            }
        }

        return this.ConvertProduct(products);
    }

    // Lấy gợi ý tìm kiếm
    public List<String> getSuggestions(String term, int limit) {
        if (term == null || term.trim().isEmpty()) {
            return List.of();
        }
        // Không cần cast vì PageRequest đã là Pageable
        return productRepository.findSuggestions(term, PageRequest.of(0, limit));
    }



    public List<ProductDto> getFeaturedProducts(int limit) {

        Pageable pageable = PageRequest.of(0, limit, Sort.by("productID").ascending()); // HOẶC Sort.by("id").ascending()

        // Lấy danh sách Product entity từ repository
        List<Product> featuredProductEntities = productRepository.findAll(pageable).getContent();

        // Sử dụng lại phương thức ConvertProduct hiện có của bạn
        return this.ConvertProduct(featuredProductEntities);
    }

    public List<ProductDto> getRelatedProducts(Long currentProductId, Long categoryId, int limit) {
        if (categoryId == null) {
            return new ArrayList<>(); // Hoặc trả về sản phẩm ngẫu nhiên nếu không có categoryId
        }
        Category category = categoryRepository.findById(categoryId)
                .orElse(null);

        if (category == null) {
            return new ArrayList<>();
        }

        List<Product> productsInCategory = productRepository.findByCategoryAndProductIDNot(category, currentProductId, PageRequest.of(0, limit));


        return this.ConvertProduct(productsInCategory); // Sử dụng lại phương thức ConvertProduct của bạn
    }


    public List<ProductDto> GetAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable); // productRepository là JpaRepository<Product, Long>
        return this.ConvertProduct(productPage.getContent()); // Sử dụng lại phương thức ConvertProduct hiện có
    }
}