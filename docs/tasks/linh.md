# Nguyễn Văn Linh – Tìm sân trống và đặt sân (xử lý đồng thời)

Nhánh: `linh/dat-san-dong-thoi`. Mã tham khảo: repo SportBooking-Team-Linh/SportBooking, tag `v1-tham-khao`.

## Module

Trang chủ tìm sân trống theo môn / ngày / giờ, chọn các giờ liền nhau, xem lại trước khi đặt, tạo đơn đặt sân. Đây là phần **xử lý truy cập đồng thời vào dữ liệu dùng chung**: nhiều khách đặt cùng một khung giờ thì chỉ một người thắng.

## File cũ → file mới

| File cũ | File mới trong v2 |
|---|---|
| `entity/Booking.java`, `entity/BookingDetail.java`, `entity/SlotReservation.java` | giữ tên |
| `repository/SlotReservationRepository.java` – `existsByCourtIdAndBookingDateAndTimeSlotId`, `findByCourtIdAndBookingDate`, `findByBookingDate`, `deleteByBookingId`, `countByCourtIdAndBookingDate` | giữ tên |
| `repository/BookingDetailRepository.java` – `existsByTimeSlotId` | giữ tên |
| `service/AvailabilityService.java` – `home`, `cells` và các record `DayOption`, `SlotCell`, `CourtCard`, `Home` | giữ tên |
| `dto/BookingForm.java` | giữ tên |
| `templates/home.html`, `preview.html`, `booking.html` | giữ đường dẫn |
| `static/js/home.js`, `static/js/booking.js`, `static/css/home.css` | giữ đường dẫn |
| `src/test/java/com/sportbooking/BookingRulesTest.java`, `AvailabilityServiceTest.java` | giữ tên |

### Phương thức lấy từ file dùng chung

| File cũ | Phương thức | Lớp mới |
|---|---|---|
| `controller/WebController.java` | `home()` (`GET /`), `parse()` | `controller/HomeController.java` |
| `controller/WebController.java` | `startBooking()` (`GET /booking/start`), `preview()` (`POST /booking/preview`), `book()` (`POST /booking`) | `controller/BookingController.java` |
| `service/BookingService.java` | `staff()`, `visible()`, `selected()`, `quote()`, `create()` | giữ trong `service/BookingService.java` |
| `SqlServerIntegrationTest.java` | `homeFinderAndBookingStartFlow`, `pastInactiveAndDiscontinuous`, `previewValidationAndTwoSlotBooking`, `tenConcurrentBookingsExactlyOneWinner`, helper `form()` | `src/test/.../BookingIntegrationTest.java` |

`BookingService.create()` dùng `CourtRepository.lockById` (của Hoàng) và ghi `BookingEvent` qua `event()` (của Kiệt): thống nhất chữ ký với hai bạn trước khi code.

## Việc cải tiến bắt buộc

- **In tên luồng ra log** khi tạo đơn (`Thread.currentThread().getName()`), để khi demo thấy rõ nhiều luồng HTTP cùng xử lý.
- **Chạy test tranh chấp hai lần để so sánh:** có khóa `PESSIMISTIC_WRITE` (đúng 1 đơn thắng) và tạm bỏ khóa (thấy ràng buộc UNIQUE phải chặn, hoặc xuất hiện lỗi). Ghi kết quả cả hai vào báo cáo chương 4.
- Giữ đúng thứ tự khóa của nhóm: khóa `Court` trước, rồi mới khóa `Booking`.

## Hạng mục rubric

1.4 (sơ đồ tuần tự đặt sân), 1.5 (phân tích đồng thời), 2.3, 3.3, 5.1, 5.2.

## Phần báo cáo

- Chương 2 – Thiết kế phần đồng thời: vì sao cần, tài nguyên dùng chung (lịch sân: court + ngày + khung giờ), cách đồng bộ (khóa hàng sân + UNIQUE). Dùng `docs/diagrams/concurrency.svg`, `booking-sequence.svg`.
- Chương 3 – Mã tiêu biểu `BookingService.create`, giao diện trang chủ và trang xem lại.
- Chương 4 – Kiểm thử 10 luồng đặt cùng một khung giờ, bảng so sánh có khóa / không khóa, log tên luồng.

## Câu hỏi vấn đáp cần chuẩn bị

1. Tài nguyên dùng chung trong bài toán đặt sân là gì? Nếu không đồng bộ thì xảy ra lỗi gì?
2. `PESSIMISTIC_WRITE` sinh ra câu SQL gì trên SQL Server? Khóa được nhả khi nào?
3. Vì sao đã khóa trong Java mà vẫn cần `UNIQUE(court_id, booking_date, time_slot_id)`?
4. `CountDownLatch` trong test dùng để làm gì? Tại sao phải để 10 luồng chờ rồi mới chạy cùng lúc?
5. `@Transactional` rollback trong trường hợp nào? Ngoại lệ checked có rollback không?
6. Giá được "chốt" vào `booking_details.price` để làm gì?

## Lệnh kiểm thử

```powershell
. ./.env.local.ps1
./mvnw.cmd -B '-Dtest=BookingRulesTest,AvailabilityServiceTest' test
$env:RUN_SQLSERVER_TESTS='true'; ./mvnw.cmd -B '-Dtest=BookingIntegrationTest#tenConcurrentBookingsExactlyOneWinner' test
```

## Checklist

- [ ] `entity/Booking.java`
- [ ] `entity/BookingDetail.java`
- [ ] `entity/SlotReservation.java`
- [ ] `repository/SlotReservationRepository.java`
- [ ] `repository/BookingDetailRepository.java`
- [ ] `service/AvailabilityService.java`
- [ ] `service/BookingService.java` (`staff`, `visible`, `selected`, `quote`, `create`)
- [ ] `dto/BookingForm.java`
- [ ] `controller/HomeController.java`
- [ ] `controller/BookingController.java`
- [ ] `templates/home.html`
- [ ] `templates/preview.html`
- [ ] `templates/booking.html`
- [ ] `static/js/home.js`
- [ ] `static/js/booking.js`
- [ ] `static/css/home.css`
- [ ] `BookingRulesTest.java`
- [ ] `AvailabilityServiceTest.java`
- [ ] `BookingIntegrationTest.java`
