# Ngô Tuấn Kiệt – Lịch sân, quản lý đơn, thanh toán, thống kê, tác vụ nền

Nhánh: `kiet/quan-ly-don`. Mã tham khảo: repo SportBooking-Team-Linh/SportBooking, tag `v1-tham-khao`.

## Module

Trang lịch của từng sân, danh sách và chi tiết đơn, vòng đời đơn (xác nhận, từ chối, hủy, nhận sân, thanh toán), nhật ký xử lý đơn, trang tổng quan / thống kê doanh thu, tác vụ nền `@Scheduled` tự từ chối đơn chờ quá giờ.

## File cũ → file mới

| File cũ | File mới trong v2 |
|---|---|
| `entity/Payment.java`, `entity/BookingEvent.java` | giữ tên |
| `repository/PaymentRepository.java` – `existsByBookingId`, `totalRevenue`, `revenue`, `daily` | giữ tên |
| `repository/BookingEventRepository.java` – `findByBookingIdOrderByCreatedAtAsc` | giữ tên |
| `repository/BookingRepository.java` – `countByBookingDate`, `countByStatus`, `countByBookingDateAndStatus`, `dailyCounts`, `in`, `lockById`, `search`, `existsByCourtId`, `findByStatusAndBookingDateLessThanEqual`, `courtIdOf`, `findByCodeAndCustomerId`, `countByCourtIdAndBookingDate` | giữ tên |
| `service/DashboardService.java` – `summary` | giữ tên |
| `service/UiFormats.java` – `money`, `date`, `time`, `dateTime`, `status`, `event`, `dayMonth`, `dayLabel`, `dayPhrase`, `weekdayShort`, `shortDate`, `longDate`, `sportKey`, `photoSmall`, `photo`, `times`, `cancellable`, `past` | giữ tên |
| `scheduling/PendingBookingExpiryJob.java` – `run` | giữ tên |
| `templates/court.html`, `bookings.html`, `dashboard.html` | giữ đường dẫn |
| `static/js/dashboard.js`, `static/css/customer.css` | giữ đường dẫn |
| `src/test/java/com/sportbooking/BookingExpiryTest.java` | giữ tên |

### Phương thức lấy từ file dùng chung

| File cũ | Phương thức | Lớp mới |
|---|---|---|
| `controller/WebController.java` | `court()` (`GET /courts/{id}`) | `controller/CourtScheduleController.java` |
| `controller/WebController.java` | `bookings()` (`/bookings`, `/staff/bookings`), `booking()` (`/bookings/{id}`), `status()`, `pay()` | `controller/BookingManageController.java` |
| `controller/WebController.java` | `dashboard()` (`/admin`, `/staff`, `/admin/statistics`) | `controller/DashboardController.java` |
| `service/BookingService.java` | `transition()`, `pay()`, `overduePending()`, `expire()`, `start()`, `systemAccount()`, `event()` | `service/BookingLifecycleService.java` (mới) |
| `SqlServerIntegrationTest.java` | `bookingLifecycleAndPriceSnapshot`, `rejectionReleasesReservation`, `paymentIsIdempotentAndOwnershipProtected`, `transactionRollsBackEvenAfterInserts`, `overduePendingBookingIsAutoRejected`, `databaseUniqueConstraintAndFutureCheckinGuard`, helper `form()` | `src/test/.../BookingLifecycleIntegrationTest.java` |

`event()` cũng được `BookingService.create()` (của Linh) gọi khi tạo đơn: để `event()` ở `BookingLifecycleService` dạng public hoặc tách `BookingEventService`, thống nhất với Linh.

## Đã có sẵn trong `main` (phần của Đạt)

- `UiContext` không còn tạo bean `ui`: nhãn vai trò dùng `roles[...]`. Bạn thêm `UiFormats` và đưa nó vào Model bằng một `@ControllerAdvice` riêng (ví dụ `UiFormatsAdvice` với `@ModelAttribute("ui")`), không sửa `UiContext`.
- Fragment `dateField` và `courtCard` trong `fragments.html` dùng `ui.date`, `ui.money`, `ui.sportKey`: chỉ hoạt động khi `UiFormats` của bạn đã vào `main`.

## Việc cải tiến bắt buộc

- **Log tên luồng của `@Scheduled`** (cấu hình `spring.task.scheduling.thread-name-prefix=sportbooking-job-`), demo thấy `[sportbooking-job-1]` chạy song song với luồng HTTP.
- **Vẽ lại sơ đồ kiến trúc** `docs/diagrams/architecture.puml` theo các controller mới của cả nhóm, xuất lại `architecture.svg`.
- Mọi thao tác đổi trạng thái khóa `Court` trước rồi mới khóa `Booking`.

## Hạng mục rubric

1.4 (sơ đồ kiến trúc), 1.5 (tác vụ nền), 2.3, 3.4 (danh sách đơn có tìm kiếm, lọc, phân trang), 4.5, 5.1.

## Phần báo cáo

- Chương 2 – Kiến trúc Controller – Service – Repository – View; vòng đời đơn (`booking-state.svg`), quy trình nghiệp vụ (`booking-activity.svg`), tác vụ nền (`scheduled-job.svg`).
- Chương 3 – Mã tiêu biểu `transition`, `pay`, `expire`; giao diện lịch sân, danh sách đơn, chi tiết đơn, tổng quan.
- Chương 4 – Kịch bản vòng đời đơn, thanh toán hai lần, rollback giữa chừng, tự từ chối đơn quá hạn.

## Câu hỏi vấn đáp cần chuẩn bị

1. Đơn đi qua những trạng thái nào? Trạng thái nào là kết thúc và không quay lại được?
2. Vì sao hủy / từ chối thì xóa `slot_reservations` nhưng giữ `bookings` và `booking_details`?
3. Làm sao đảm bảo một đơn không bị thanh toán hai lần (cả trong Java lẫn ràng buộc SQL)?
4. `@Scheduled(fixedDelay)` khác `fixedRate` và `cron` thế nào? Job chạy trên luồng nào?
5. Nếu nhân viên đang xác nhận đơn đúng lúc job tự từ chối đơn đó thì chuyện gì xảy ra?
6. Doanh thu tính theo ngày thanh toán, còn lượt đặt tính theo ngày chơi: vì sao?

## Lệnh kiểm thử

```powershell
. ./.env.local.ps1
./mvnw.cmd -B '-Dtest=BookingExpiryTest' test
$env:RUN_SQLSERVER_TESTS='true'; ./mvnw.cmd -B '-Dtest=BookingLifecycleIntegrationTest' test
```

## Checklist

- [ ] `entity/Payment.java`
- [ ] `entity/BookingEvent.java`
- [ ] `repository/PaymentRepository.java`
- [ ] `repository/BookingEventRepository.java`
- [ ] `repository/BookingRepository.java`
- [ ] `service/BookingLifecycleService.java`
- [ ] `service/DashboardService.java`
- [ ] `service/UiFormats.java`
- [ ] `scheduling/PendingBookingExpiryJob.java`
- [ ] `controller/CourtScheduleController.java`
- [ ] `controller/BookingManageController.java`
- [ ] `controller/DashboardController.java`
- [ ] `templates/court.html`
- [ ] `templates/bookings.html`
- [ ] `templates/dashboard.html`
- [ ] `static/js/dashboard.js`
- [ ] `static/css/customer.css`
- [ ] `BookingExpiryTest.java`
- [ ] `BookingLifecycleIntegrationTest.java`
- [ ] `docs/diagrams/architecture.puml` + `.svg` vẽ lại
