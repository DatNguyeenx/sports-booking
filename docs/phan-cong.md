# Bảng phân công – SportBooking phiên bản 2

Phiên bản 2 được tái cấu trúc từ dự án SportBooking-Team-Linh/SportBooking, mốc tham khảo là tag `v1-tham-khao`. Mỗi thành viên phụ trách một module dọc, từ cơ sở dữ liệu tới giao diện, và tự viết lại phần của mình vào repo này theo cấu trúc mới (controller tách nhỏ, nghiệp vụ trong service).

| Thành viên | MSSV | Nhánh | Module | Danh sách việc |
|---|---|---|---|---|
| Nguyễn Tiến Đạt (nhóm trưởng) | 2001240072 | `dat/nen-tang-tai-khoan` | Nền tảng, bảo mật, tài khoản | [tasks/dat.md](tasks/dat.md) |
| Đỗ Minh Hoàng | 2001240143 | `hoang/danh-muc-csdl` | Danh mục sân và cơ sở dữ liệu | [tasks/hoang.md](tasks/hoang.md) |
| Nguyễn Văn Linh | 2001240247 | `linh/dat-san-dong-thoi` | Tìm sân trống, đặt sân, xử lý đồng thời | [tasks/linh.md](tasks/linh.md) |
| Ngô Tuấn Kiệt | 2001240231 | `kiet/quan-ly-don` | Lịch sân, quản lý đơn, thanh toán, thống kê, tác vụ nền | [tasks/kiet.md](tasks/kiet.md) |
| Vương Hoàng Phúc | 2001240377 | `phuc/mang-giao-dien` | Lập trình mạng, giao diện chung, đóng gói | [tasks/phuc.md](tasks/phuc.md) |

## Thứ tự làm và phụ thuộc

1. **Đạt và Hoàng làm trước** (tuần đầu): Đạt dựng `SecurityConfig`, `User`, `fragments.html`, xử lý lỗi; Hoàng dựng script SQL, entity `SportType`, `Court`, `TimeSlot`. Hai phần này merge sớm vì các bạn khác cần.
2. **Linh và Kiệt** dựa trên entity của Hoàng: Linh làm `Booking`, `BookingDetail`, `SlotReservation` và luồng đặt sân; Kiệt làm `Payment`, `BookingEvent`, vòng đời đơn và thống kê. Hai bạn thống nhất chữ ký `BookingService` / `BookingLifecycleService` trước khi code.
3. **Phúc** làm song song: CSS chung, `app.js`, socket server/client, script chạy và kiểm thử.

## Quy ước chung

- **Package gốc:** `com.sportbooking`, giữ các package `config`, `controller`, `dto`, `entity`, `exception`, `networking`, `repository`, `scheduling`, `security`, `service`.
- **Controller mỏng:** chỉ nhận request, `@Valid`, gọi service, đưa dữ liệu vào `Model`. Nghiệp vụ nằm trong service, thao tác nhiều bước có `@Transactional`.
- **Thứ tự khóa thống nhất:** luôn khóa `Court` trước (`CourtRepository.lockById`), rồi mới khóa `Booking` (`BookingRepository.lockById`). Không đảo thứ tự để tránh deadlock.
- **Dữ liệu dùng chung cho mọi trang** (`types`, `me`): Đạt chuyển `WebController.common()` và `AdminController.common()` thành một `@ControllerAdvice` (`GlobalModelAdvice`) để các controller mới không phải lặp lại.
- **Giao diện, thông báo lỗi, comment, tài liệu:** tiếng Việt có dấu.
- **Không commit:** `.env.local.ps1`, mật khẩu, `target/`, `.idea/`.

## Quy trình nhánh và Pull Request

1. Làm trên nhánh của mình, commit nhỏ, message tiếng Việt, mô tả đúng việc đã làm.
2. Trước khi mở PR: `git fetch origin` rồi `git merge origin/main` vào nhánh mình, chạy `./mvnw.cmd -B clean test` không lỗi.
3. Mở Pull Request vào `main`, ghi mục đã làm trong `docs/tasks/<tên>.md`. Đạt duyệt và merge.
4. Mỗi thành viên commit đều đặn trong suốt quá trình (rubric 6.4).

## Tỉ lệ đóng góp

| Thành viên | Tỉ lệ dự kiến | Xác nhận |
|---|---|---|
| Nguyễn Tiến Đạt | 20% | |
| Đỗ Minh Hoàng | 20% | |
| Nguyễn Văn Linh | 20% | |
| Ngô Tuấn Kiệt | 20% | |
| Vương Hoàng Phúc | 20% | |

Tỉ lệ cuối cùng cập nhật theo lịch sử Git và kết quả thực tế, cả nhóm ký xác nhận trước khi nộp.
