# Đăng nhập và phân quyền

Ứng dụng xác thực bằng bảng `dbo.users`, sử dụng BCrypt cho cột `password_hash`. Tài khoản có `enabled = 0` không đăng nhập được. Không còn dùng tài khoản `user` và mật khẩu tự sinh của Spring Security.

## Tài khoản demo trên máy phát triển

Chạy `database/auth-demo.sql` sau `database/schema.sql` trong SSMS. Script chỉ thêm tài khoản còn thiếu, không đổi mật khẩu hoặc vai trò của tài khoản đã tồn tại.

| Tên đăng nhập | Mật khẩu demo | Vai trò |
|---|---|---|
| admin_demo | Admin@12345 | ADMIN |
| staff_demo | Staff@12345 | STAFF |
| customer_demo | Customer@12345 | CUSTOMER |

Các tài khoản này chỉ dùng cho dữ liệu demo. Mỗi thành viên tự chạy script trên SQL Server của mình.

## Quyền truy cập

| Đường dẫn | Quyền |
|---|---|
| `/`, `/courts`, `/login` | Không cần đăng nhập |
| `/account` | Mọi tài khoản đã đăng nhập |
| `/admin`, `/admin/**` | ADMIN |
| `/staff`, `/staff/**` | STAFF hoặc ADMIN |
| `/customer/**`, `/bookings/**` | CUSTOMER |

Các trang `/admin` và `/staff` hiện dùng để xác nhận quyền truy cập; chức năng quản lý nghiệp vụ sẽ được bổ sung sau. Chưa có chức năng đăng ký, sửa hồ sơ hoặc quản lý tài khoản.

Đăng xuất dùng form POST `/logout` có CSRF token. Thymeleaf tự chèn token cho form có `th:action` và `method="post"`. Không tắt CSRF để giải quyết lỗi form.

`AppUserDetails` giữ ID, tên và vai trò để các Service về sau có thể kiểm tra chủ sở hữu đơn bằng ID đăng nhập. Các matcher URL chưa thay thế được việc kiểm tra chủ đơn trong Service.

## Chạy kiểm thử

Tạo `application-local.properties` và bật SQL Server trước khi chạy:

```powershell
.\mvnw.cmd -Dspring.profiles.active=local test
```

Kiểm thử đăng nhập tạo tài khoản riêng trong transaction rồi rollback, không để lại tài khoản thử trong database.
