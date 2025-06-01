
## Các bước cài đặt và chạy dự án

1.  **Sử dụng IntelliJ IDEA để chạy**
    *   Mở IntelliJ IDEA.

2.  **Clone dự án từ GitHub:**
        ```bash
        git clone https://github.com/doihdue/TTCS_D22.git
        ```

3.  **Tạo Database MySQL bằng MySQL Workbench:**
    *   Mở MySQL Workbench và kết nối tới server MySQL.
    *   Tạo một schema (database) mới. Ví dụ: đặt tên là `ttcs_d22_db`.
        ```sql
        CREATE DATABASE ttcs_d22_db;
        ```

4.  **Điền thông tin vào file `application.properties`:**
    *   Trong IntelliJ IDEA, mở file `src/main/resources/application.properties`.
    *   Cập nhật các thông tin sau:
        ```properties
        # Database Configuration
        spring.datasource.url=jdbc:mysql://localhost:3306/{database_name}  # Thay {database_name} bằng tên database đã tạo ở bước 3 (ví dụ: ttcs_d22_db)
        spring.datasource.username={your_mysql_username}                   # Thay bằng username MySQL (ví dụ: root)
        spring.datasource.password={your_mysql_password}                   # Thay bằng password MySQL 

        # Spring Boot specific properties (nếu có, ví dụ tự động tạo bảng)
        spring.jpa.hibernate.ddl-auto=update # Hoặc create, create-drop. 'update' sẽ tạo bảng nếu chưa có hoặc cập nhật cấu trúc.
        spring.jpa.show-sql=true

        # Gửi mail (Cấu hình Gmail)
        spring.mail.host=smtp.gmail.com
        spring.mail.port=587
        spring.mail.username={your_gmail_address}          # Thay bằng địa chỉ email Gmail c
        spring.mail.password={your_gmail_app_password}     # Thay bằng App Password từ tài khoản Google 
        spring.mail.properties.mail.smtp.auth=true
        spring.mail.properties.mail.smtp.starttls.enable=true
        ```

5.  **Chạy project:**
    *   Trong IntelliJ IDEA, tìm đến class chính có annotation `@SpringBootApplication` (thường là `TtcsD22Application.java` hoặc tương tự).
    *   Click chuột phải vào file đó và chọn "Run '...Application'".
    *   Sau khi project khởi động thành công (thường sẽ có log "Tomcat started on port(s): 8080 (http)"), vào trang web với đường link: `http://localhost:8080/`

## Thiết lập Dữ liệu Ban đầu (Thủ công)

*Do chức năng seed data tự động khi tạo mới database chưa hoàn thiện, cần thực hiện các bước sau thủ công thông qua MySQL Workbench (hoặc công cụ tương tự) sau khi database đã được tạo và dự án đã chạy ít nhất một lần để Spring Boot tạo các bảng.*

6.  **Vào bảng `role` thêm dữ liệu:**
    *   Mở MySQL Workbench, chọn schema database.
    *   Mở bảng `role` (hoặc `roles`).
    *   Thêm các dòng sau (hoặc chạy lệnh SQL):
        ```sql
        INSERT INTO `role` (id, name) VALUES
        (1, 'admin'),
        (2, 'user');
        ```

7.  **Vào bảng `categories` thêm dữ liệu:**
    *   Tương tự, mở bảng `categories`.
    *   Thêm các dòng sau (hoặc chạy lệnh SQL):
        ```sql
        INSERT INTO `categories` (id, name) VALUES
        (1, 'Hạt Dinh Dưỡng'),
        (2, 'Trái Cây Sấy'),
        (3, 'Nguyên Liệu Làm Bánh');
        -- Tương tự, kiểm tra cấu trúc bảng `categories` cho cột ID.
        ```

8.  **Tạo tài khoản Admin:**
    *   Truy cập trang web `http://localhost:8080/`.
    *   Sử dụng chức năng **Đăng ký** (Register) để tạo một tài khoản người dùng mới.
    *   Sau khi đăng ký thành công, vào MySQL Workbench, mở bảng `users` (hoặc tên tương tự).
    *   Tìm dòng tương ứng với user vừa tạo.
    *   Sửa giá trị của cột `role_id` (hoặc tên cột foreign key trỏ đến bảng `role`) của user đó thành `1` (ID của role "admin" đã thêm ở bước 6).
        Ví dụ lệnh SQL (giả sử user vừa đăng ký có `id` là `X` hoặc `username` là `your_username`):
        ```sql
        -- Cập nhật dựa trên ID (nếu biết)
        UPDATE users SET role_id = 1 WHERE id = X;

        -- Hoặc cập nhật dựa trên username
        -- UPDATE users SET role_id = 1 WHERE username = 'your_registered_username';
        ```
    *   Sau khi cập nhật, tài khoản này sẽ có quyền Admin.
