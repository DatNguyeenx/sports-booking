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

Ứng dụng đang dùng đăng nhập mặc định của Spring Security:

- Username: `user`
- Password: xem dòng `Using generated security password` trong log.

Mật khẩu có thể thay đổi mỗi lần khởi động lại ứng dụng.

## Kiểm tra biên dịch

Trong Terminal PowerShell:

```powershell
.\mvnw.cmd compile
```

## Trạng thái hiện tại

- Có bộ khung Spring Boot và trang chủ.
- Đã chạy thử kết nối SQL Server trên máy nhóm trưởng.
  - Chưa triển khai bảng nghiệp vụ và chức năng đặt sân.