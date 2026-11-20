# Sports Booking – phiên bản 2

Đồ án Công nghệ Java — Website đặt sân thể thao cho một cơ sở có nhiều sân bóng đá, cầu lông, pickleball.

Phiên bản 2 được tái cấu trúc từ dự án SportBooking-Team-Linh/SportBooking (tag `v1-tham-khao`). Bản thử nghiệm đầu tiên của repo này được giữ ở tag `v0-thu-nghiem`.

## Phạm vi

- Một cơ sở, nhiều sân, giá tính theo giờ, múi giờ Asia/Ho_Chi_Minh.
- Khách đặt một hoặc nhiều giờ liền nhau; nhân viên xác nhận, nhận sân, thu tiền; quản trị viên quản lý danh mục và tài khoản.
- Đặc tả chi tiết: [docs/requirements.md](docs/requirements.md). Sơ đồ thiết kế: [docs/diagrams](docs/diagrams).

## Công nghệ

- JDK 21 (biên dịch với `--release 17`)
- Spring Boot 3.5: Web MVC, Security, Data JPA, Validation, Thymeleaf
- Microsoft SQL Server
- Maven Wrapper (`mvnw.cmd`), không cần cài Maven
- Socket TCP (ServerSocket) và chương trình client Java tách biệt

## Chuẩn bị database

1. Cài SQL Server và SQL Server Management Studio.
2. Tạo database `SportBookingDB`.
3. Tạo tài khoản SQL `sportbooking_app`, ánh xạ vào database.
4. Cấp quyền `db_datareader` và `db_datawriter` (chỉ đọc / ghi dữ liệu, không cấp quyền sửa cấu trúc bảng).
5. Bật TCP/IP trong SQL Server Configuration Manager, cấu hình cổng (thường là 1433) và khởi động lại SQL Server.
6. Chạy các script trong thư mục `sql/` theo thứ tự số (tạo bảng, dữ liệu mẫu, kiểm tra).

Ứng dụng dùng `spring.jpa.hibernate.ddl-auto=validate`: Hibernate không tự tạo hay sửa bảng, mọi thay đổi cấu trúc phải đưa vào script SQL.

## Cấu hình trên từng máy

Thông tin kết nối đọc từ biến môi trường, không ghi trong mã nguồn.

1. Sao chép `.env.example.ps1` thành `.env.local.ps1` (file này đã được `.gitignore` bỏ qua, **không commit**).
2. Điền `DB_HOST`, `DB_PORT`, `DB_USERNAME`, `DB_PASSWORD`, `DEMO_PASSWORD` của máy mình.
3. Mỗi lần mở cửa sổ PowerShell mới, nạp biến trước khi chạy:

```powershell
. ./.env.local.ps1
```

`DB_PORT` không có giá trị mặc định: thiếu biến này ứng dụng sẽ không kết nối được database.

## Build và chạy

```powershell
. ./.env.local.ps1
./mvnw.cmd -B package
java -jar target/SportBooking-1.0.0.jar
```

Truy cập http://localhost:8080.

### Chạy bằng IntelliJ IDEA

1. Open thư mục dự án, đợi Maven tải thư viện, chọn Project SDK là JDK 21.
2. Run → Edit Configurations → tạo cấu hình Application cho lớp `com.sportbooking.SportBookingApplication`.
3. Trong Environment variables, nhập các biến giống `.env.local.ps1` (IntelliJ không tự đọc file `.ps1`).
4. Cấu hình chạy nằm trong `.idea/` nên không bị commit.

## Kiểm thử

```powershell
./mvnw.cmd -B clean test                                  # unit test
$env:RUN_SQLSERVER_TESTS='true'; ./mvnw.cmd -B clean test # thêm test tích hợp với SQL Server thật
```

## Phân công và quy trình làm việc

Bảng phân công, nhánh của từng thành viên và danh sách việc: [docs/phan-cong.md](docs/phan-cong.md).

1. Mỗi thành viên làm trên nhánh riêng (`<tên>/<module>`), commit nhỏ, message tiếng Việt.
2. Trước khi gửi: `git fetch origin`, `git merge origin/main`, chạy `./mvnw.cmd -B clean test` không lỗi.
3. Mở Pull Request vào `main`; nhóm trưởng duyệt và merge. Không push thẳng lên `main`.
4. Không commit `.env.local.ps1`, mật khẩu, `target/`, `.idea/`.

## Trạng thái hiện tại

- Đã có khung Spring Boot 3.5, cấu hình theo profile `local` / `prod`, tài liệu thiết kế và sơ đồ.
- Các module (tài khoản, danh mục, đặt sân, quản lý đơn, mạng) đang được từng thành viên chuyển sang theo [docs/phan-cong.md](docs/phan-cong.md).
