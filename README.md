1. Sử dụng IntelliJ IDEA để chạy
2. Clone dự án từ GitHub: https://github.com/doihdue/TTCS_D22.git
3. Tạo Database MySQL bằng MySQL Workbench
4. Điền thông tin vào file application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/{database name}
spring.datasource.username=
spring.datasource.password=
# Gửi mail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=
spring.mail.password=
5. Chạy project rồi vào trang web với đường link: http://localhost:8080/
6. Vào bảng role thêm:
id     name
1      admin
2      user
7. Vào bảng categories thêm:
id     name
1      Hạt Dinh Dưỡng
2      Trái Cây Sấy
3      Nguyên Liệu Làm Bánh
8. Tạo 1 tài khoản user ở chức năng đăng kí khi đăng kí xong vào bảng users sửa role_id thành "1" để thành tài khoản admin
(seed data khi tạo mới database em đã làm nhưng chưa thành công nên hiện tại phải làm như này ạ)
