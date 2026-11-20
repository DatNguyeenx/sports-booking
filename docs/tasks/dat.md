# Nguyễn Tiến Đạt – Nền tảng, bảo mật, tài khoản

Nhánh: `dat/nen-tang-tai-khoan`. Mã tham khảo: repo SportBooking-Team-Linh/SportBooking, tag `v1-tham-khao` (đường dẫn bên dưới tính từ gốc repo đó).

## Module

Khung ứng dụng (cấu hình, khởi động), Spring Security (đăng nhập bằng email, BCrypt, phân quyền ADMIN/STAFF/CUSTOMER, CSRF), thu hồi phiên khi tài khoản bị khóa / đổi vai trò, đăng ký, hồ sơ cá nhân, quản lý tài khoản của admin, xử lý lỗi và trang lỗi, bố cục dùng chung (`fragments.html`).

## File cũ → file mới

| File cũ | File mới trong v2 |
|---|---|
| `pom.xml`, `src/main/resources/application.properties`, `application-local.properties`, `application-prod.properties` | Giữ nguyên (đã có trong `main`), chỉ chỉnh khi cần |
| `src/main/resources/ValidationMessages.properties` | Giữ nguyên |
| `src/main/java/com/sportbooking/SportBookingApplication.java` | Đã có trong `main` |
| `config/SecurityConfig.java` – `normalizeEmail`, `encoder`, `users`, `security` | `config/SecurityConfig.java` |
| `security/AccountStateFilter.java` – `shouldNotFilter`, `doFilterInternal` | `security/AccountStateFilter.java` |
| `entity/User.java` | `entity/User.java` |
| `repository/UserRepository.java` – `findByEmail`, `existsByEmail`, `countByRoleAndEnabledTrue`, `countByRole`, `lower`, `findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase` | `repository/UserRepository.java` |
| `service/AccountService.java` – `current`, `register`, `profile`, `manage`, `delete` | `service/AccountService.java` |
| `controller/UiContext.java` – `formats`, `presentation` | `controller/UiContext.java` |
| `dto/RegistrationForm.java`, `dto/ProfileForm.java` | giữ tên |
| `exception/BusinessException.java`, `exception/WebExceptionHandler.java` – `business`, `integrity`, `input` | giữ tên |
| `templates/fragments.html`, `login.html`, `register.html`, `profile.html`, `admin/users.html`, `error/403.html`, `404.html`, `500.html`, `business.html` | giữ đường dẫn |
| `src/test/java/com/sportbooking/SecurityConfigTest.java` | giữ tên |

### Phương thức lấy từ file dùng chung

| File cũ | Phương thức | Lớp mới |
|---|---|---|
| `controller/WebController.java` | `common()` | `controller/GlobalModelAdvice.java` (`@ControllerAdvice`) |
| `controller/WebController.java` | `login()`, `registration()`, `register()`, `forbidden()` | `controller/AuthController.java` |
| `controller/WebController.java` | `profile()` GET, `profile()` POST | `controller/ProfileController.java` |
| `controller/AdminController.java` | `common()` | gộp vào `GlobalModelAdvice` |
| `controller/AdminController.java` | `users()`, `user()` (lưu), `deleteUser()`, helper `flash()` | `controller/AdminUserController.java` |
| `SqlServerIntegrationTest.java` | `registerAndLogin`, `permissionsAndCsrf`, `renderPublicPages`, `redesignedAssetsAndVietnamesePresentation`, `renderRolePages`, `accountCrudAndSessionRevocation` | `src/test/.../AuthIntegrationTest.java` |

## Việc cải tiến bắt buộc

- Tách `WebController` / `AdminController` cũ thành `AuthController`, `ProfileController`, `AdminUserController` và `GlobalModelAdvice` cho dữ liệu dùng chung (`types`, `me`).
- Giữ một `README.md` duy nhất ở gốc repo, cập nhật khi các bạn thêm chức năng.
- Duyệt và merge Pull Request của cả nhóm, kiểm tra không ai commit `.env.local.ps1`, `target/`, `.idea/`.

## Hạng mục rubric

1.1, 1.2 (bảng chức năng, use case), 2.1, 2.2, 2.5, 3.1, 3.5, 4.4 (CRUD tài khoản), 6.1, 6.2, 6.4.

## Phần báo cáo

- Chương 1 – Tổng quan (bài toán, mục tiêu, phạm vi, công cụ).
- Chương 2 – Bảng chức năng theo vai trò, sơ đồ use case, yêu cầu phi chức năng.
- Chương 3 – Cấu trúc dự án, cấu hình Spring Security, giao diện đăng nhập / đăng ký / hồ sơ / quản lý tài khoản.
- Chương 4 – Kịch bản kiểm thử phân quyền, CSRF, thu hồi phiên.
- Chương 5 – Kết luận; phụ lục bảng phân công.

## Câu hỏi vấn đáp cần chuẩn bị

1. Spring Security xác thực một request đăng nhập qua những bước nào? `UserDetailsService` trả về gì?
2. Vì sao mật khẩu lưu BCrypt mà không mã hóa hai chiều? Salt nằm ở đâu?
3. CSRF là gì, vì sao logout phải là POST có token?
4. `AccountStateFilter` làm gì khi admin khóa tài khoản đang đăng nhập?
5. `@ControllerAdvice` dùng cho xử lý lỗi và cho dữ liệu dùng chung khác nhau thế nào?
6. Phân quyền theo URL (`/admin/**`, `/staff/**`) và kiểm tra chủ sở hữu đơn trong service khác nhau ra sao?

## Lệnh kiểm thử

```powershell
. ./.env.local.ps1
./mvnw.cmd -B '-Dtest=SecurityConfigTest' test
$env:RUN_SQLSERVER_TESTS='true'; ./mvnw.cmd -B '-Dtest=AuthIntegrationTest' test
```

## Checklist

- [ ] `config/SecurityConfig.java`
- [ ] `security/AccountStateFilter.java`
- [ ] `entity/User.java`
- [ ] `repository/UserRepository.java`
- [ ] `service/AccountService.java`
- [ ] `controller/UiContext.java`
- [ ] `controller/GlobalModelAdvice.java`
- [ ] `controller/AuthController.java`
- [ ] `controller/ProfileController.java`
- [ ] `controller/AdminUserController.java`
- [ ] `dto/RegistrationForm.java`
- [ ] `dto/ProfileForm.java`
- [ ] `exception/BusinessException.java`
- [ ] `exception/WebExceptionHandler.java`
- [ ] `ValidationMessages.properties`
- [ ] `templates/fragments.html`
- [ ] `templates/login.html`
- [ ] `templates/register.html`
- [ ] `templates/profile.html`
- [ ] `templates/admin/users.html`
- [ ] `templates/error/403.html`, `404.html`, `500.html`, `business.html`
- [ ] `SecurityConfigTest.java`
- [ ] `AuthIntegrationTest.java`
- [ ] `README.md` cập nhật
