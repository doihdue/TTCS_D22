
document.addEventListener('DOMContentLoaded', function() {
    // Back to top button functionality
    const backToTopButton = document.getElementById('backToTop');
    
    window.addEventListener('scroll', function() {
        if (window.pageYOffset > 300) {
            backToTopButton.classList.add('show');
        } else {
            backToTopButton.classList.remove('show');
        }
    });
    
    backToTopButton.addEventListener('click', function(e) {
        e.preventDefault();
        window.scrollTo({top: 0, behavior: 'smooth'});
    });
});



const placeholders = [
    "Bạn tìm sản phẩm gì?",
    "Hạt dinh dưỡng",
    "Trái cây sấy",
    "Nguyên liệu làm bánh"
];

const searchInput = document.getElementById('searchInput');
let currentPlaceholder = 0;      // Chỉ mục của cụm từ hiện tại trong mảng
let currentCharIndex = 0;        // Vị trí ký tự hiện tại đang được gõ/xóa
let isDeleting = false;          // Trạng thái: đang gõ hay đang xóa
let typingSpeed = 100;           // Tốc độ gõ (mili giây)

function typeEffect() {
    const currentText = placeholders[currentPlaceholder];
    
    if (isDeleting) {
        // Xử lý khi đang xóa văn bản
        searchInput.placeholder = currentText.substring(0, currentCharIndex - 1);
        currentCharIndex--;
        
        // Khi xóa hoàn tất, chuyển sang cụm từ tiếp theo
        if (currentCharIndex === 0) {
            isDeleting = false;
            currentPlaceholder = (currentPlaceholder + 1) % placeholders.length;
            typingSpeed = 100; // Đặt lại tốc độ gõ
            setTimeout(typeEffect, 500); // Tạm dừng trước khi gõ từ tiếp theo
            return;
        }
        
        typingSpeed = 50; // Tốc độ nhanh hơn khi xóa
    } else {
        // Xử lý khi đang gõ văn bản
        searchInput.placeholder = currentText.substring(0, currentCharIndex + 1);
        currentCharIndex++;
        
        // Khi gõ hoàn tất, tạm dừng rồi bắt đầu xóa
        if (currentCharIndex === currentText.length) {
            isDeleting = true;
            typingSpeed = 100;
            setTimeout(typeEffect, 1500); // Tạm dừng trước khi xóa
            return;
        }
    }
    
    setTimeout(typeEffect, typingSpeed);
}

setTimeout(typeEffect, 500);



// Lấy các phần tử nút và input
const decreaseButton = document.getElementById("decreaseQuantity");
const increaseButton = document.getElementById("increaseQuantity");
const quantityInput = document.getElementById("quantity");

// Xử lý sự kiện khi nhấn vào nút "-"
decreaseButton.addEventListener("click", function() {
    let currentValue = parseInt(quantityInput.value);
    if (currentValue > 1) {
        quantityInput.value = currentValue - 1;
    }
});

// Xử lý sự kiện khi nhấn vào nút "+"
increaseButton.addEventListener("click", function() {
    let currentValue = parseInt(quantityInput.value);
    quantityInput.value = currentValue + 1;
});
