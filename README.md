# Sports Booking

Đồ án Công nghệ Java — Website đặt sân thể thao đa môn.

## Phạm vi

- Một trung tâm có nhiều môn thể thao.
- Một đơn đặt một môn trong cùng một ngày.
- Có thể đặt nhiều sân, mỗi sân có khoảng giờ khác nhau.
- Mỗi ca dài 30 phút.

## Công nghệ

- Java 25
- Spring Boot 4.0.9
- Maven
- Thymeleaf
- Spring Data JPA
- Spring Security
- SQL Server

## Chuẩn bị database

1. Cài SQL Server và SQL Server Management Studio.
2. Tạo database `SportsBooking`.
3. Tạo tài khoản SQL `sports_app`, ánh xạ vào database.
4. Cấp quyền `db_datareader` và `db_datawriter`.
5. Bật TCP/IP, cấu hình cổng 1433 và khởi động lại SQL Server.

## Cấu hình trên từng máy

1. Sao chép file:
   `src/main/resources/application-local.properties.example`
2. Đổi tên bản sao thành:
   `application-local.properties`
3. Điền địa chỉ server, tài khoản và mật khẩu trên máy mình.
4. File cấu hình riêng đã được `.gitignore` bỏ qua.

## Chạy bằng IntelliJ IDEA

1. Open thư mục dự án, đợi Maven tải thư viện.
2. Chọn Project SDK là Java 25.
3. Mở Run → Edit Configurations.
4. Trong Environment variables, thêm:
   `SPRING_PROFILES_ACTIVE=local`
5. Chạy lớp `SportsBookingApplication`.
6. Truy cập http://localhost:8080.

## Đăng nhập hiện tại

Đăng nhập bằng tài khoản trong bảng `dbo.users`, mật khẩu băm BCrypt.
Chạy `database/auth-demo.sql` để tạo tài khoản demo trên máy mình.

| Tên đăng nhập | Mật khẩu demo | Vai trò |
|---|---|---|
| admin_demo | Admin@12345 | ADMIN |
| staff_demo | Staff@12345 | STAFF |
| customer_demo | Customer@12345 | CUSTOMER |

Mở `/login` để đăng nhập. Khách chưa đăng nhập vẫn xem được `/courts`.
Chi tiết quyền và cách kiểm thử nằm trong `docs/AUTH.md`.

## Kiểm tra biên dịch

Trong Terminal PowerShell:

```powershell
.\mvnw.cmd compile
```

## Trạng thái hiện tại

- Có script 7 bảng, dữ liệu mẫu sân và các Entity/Repository.
- Có danh sách sân, tìm kiếm, lọc môn, phân trang và sắp xếp.
- Đăng nhập từ database, BCrypt, phân quyền và đăng xuất.
- Chưa triển khai đăng ký, quản lý sân, lịch và nghiệp vụ đặt sân.