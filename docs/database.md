# Từ điển dữ liệu SQL Server

Schema có 9 bảng, IDENTITY bigint cho khóa chính, FK không cascade delete. Chuỗi người dùng NVARCHAR, tiền DECIMAL(12,2), ngày DATE, giờ TIME(0), thời điểm DATETIME2. Hibernate ddl-auto=validate.

## users

Tài khoản đăng nhập và thông tin liên hệ. Role được kiểm soát ở server; đăng ký công khai luôn là CUSTOMER. enabled=false thu hồi phiên ở request kế tiếp. Không xóa khi đã được tham chiếu trong đơn, thanh toán hoặc nhật ký.

| Cột | Java | SQL / ràng buộc |
|---|---|---|
| id | Long | bigint IDENTITY PRIMARY KEY |
| email | String | nvarchar(160) NOT NULL UNIQUE |
| password | String | varchar(100) NOT NULL |
| full_name | String | nvarchar(100) NOT NULL |
| phone | String | nvarchar(20) NOT NULL |
| role | String | varchar(20) NOT NULL CHECK(role IN ('ADMIN','STAFF','CUSTOMER')) |
| enabled | boolean | bit NOT NULL |

## sport_types

Danh mục có ba loại thực tế. Tên duy nhất. Một loại có nhiều sân; FK không cascade delete để tránh mất lịch sử. Thêm loại mới khi có sân kinh doanh tương ứng.

| Cột | Java | SQL / ràng buộc |
|---|---|---|
| id | Long | bigint IDENTITY PRIMARY KEY |
| name | String | nvarchar(80) NOT NULL UNIQUE |
| description | String | nvarchar(500) NOT NULL |

## courts

Tài nguyên được khóa pessimistic khi đặt sân. price là giá một khung giờ hiện tại, không sửa giá đơn đã đặt. active=false chặn đơn mới nhưng giữ lịch sử và cho nhân viên xử lý các đơn đã có.

| Cột | Java | SQL / ràng buộc |
|---|---|---|
| id | Long | bigint IDENTITY PRIMARY KEY |
| name | String | nvarchar(100) NOT NULL UNIQUE |
| sport_type_id | SportType | bigint NOT NULL REFERENCES sport_types(id) |
| description | String | nvarchar(1000) NOT NULL |
| price | BigDecimal | decimal(12,2) NOT NULL CHECK(price>0) |
| active | boolean | bit NOT NULL |

## time_slots

Khoảng thời gian theo giờ địa phương, không phải UTC instant. end_time phải lớn hơn start_time. CatalogService ngăn chồng lấn khi thêm; không đổi giờ của khung đã tạo, chỉ bật/tắt để bảo toàn lịch sử.

| Cột | Java | SQL / ràng buộc |
|---|---|---|
| id | Long | bigint IDENTITY PRIMARY KEY |
| start_time | LocalTime | time(0) NOT NULL UNIQUE |
| end_time | LocalTime | time(0) NOT NULL |
| active | boolean | bit NOT NULL |

## bookings

Phần đầu đơn: khách, sân, ngày, trạng thái, tổng tiền và thời điểm tạo. Code dạng UUID không thay thế quyền truy cập. details ánh xạ OneToMany tới BookingDetail; mỗi booking thuộc một khách và một sân.

| Cột | Java | SQL / ràng buộc |
|---|---|---|
| id | Long | bigint IDENTITY PRIMARY KEY |
| code | String | varchar(40) NOT NULL UNIQUE |
| customer_id | User | bigint NOT NULL REFERENCES users(id) |
| court_id | Court | bigint NOT NULL REFERENCES courts(id) |
| booking_date | LocalDate | date NOT NULL |
| status | String | varchar(20) NOT NULL CHECK(status IN ('PENDING','CONFIRMED','CHECKED_IN','COMPLETED','CANCELLED','REJECTED')) |
| total | BigDecimal | decimal(12,2) NOT NULL CHECK(total>0) |
| created_at | LocalDateTime | datetime2 NOT NULL |

## booking_details

Lưu từng khung đã đặt và giá snapshot. Không xóa khi đơn hủy. Tổng đơn bằng tổng giá các detail tại thời điểm tạo. Tham chiếu TimeSlot bất biến về giờ giúp lịch sử không bị trôi.

| Cột | Java | SQL / ràng buộc |
|---|---|---|
| id | Long | bigint IDENTITY PRIMARY KEY |
| booking_id | Booking | bigint NOT NULL REFERENCES bookings(id) |
| time_slot_id | TimeSlot | bigint NOT NULL REFERENCES time_slots(id) |
| price | BigDecimal | decimal(12,2) NOT NULL CHECK(price>0) |

## slot_reservations

Tập giữ chỗ hiệu lực. UNIQUE(court_id,booking_date,time_slot_id) bảo vệ lịch cả khi nhiều JVM cùng chạy. Hủy/từ chối xóa hàng này trong cùng transaction, không xóa detail.

| Cột | Java | SQL / ràng buộc |
|---|---|---|
| id | Long | bigint IDENTITY PRIMARY KEY |
| booking_id | Booking | bigint NOT NULL REFERENCES bookings(id) |
| court_id | Court | bigint NOT NULL REFERENCES courts(id) |
| booking_date | LocalDate | date NOT NULL |
| time_slot_id | TimeSlot | bigint NOT NULL REFERENCES time_slots(id) |

## payments

Ghi nhận thu tiền thủ công. Một payment toàn phần cho một booking, UNIQUE booking_id ngăn thu hai lần. amount bằng booking.total, method CASH/TRANSFER, nhân viên và thời gian được lưu để kiểm toán.

| Cột | Java | SQL / ràng buộc |
|---|---|---|
| id | Long | bigint IDENTITY PRIMARY KEY |
| booking_id | Booking | bigint NOT NULL UNIQUE REFERENCES bookings(id) |
| amount | BigDecimal | decimal(12,2) NOT NULL CHECK(amount>0) |
| method | String | varchar(20) NOT NULL CHECK(method IN ('CASH','TRANSFER')) |
| received_by_id | User | bigint NOT NULL REFERENCES users(id) |
| paid_at | LocalDateTime | datetime2 NOT NULL |

## booking_events

Nhật ký mỗi bước nghiệp vụ. Lưu actor, action và created_at; không dùng log ứng dụng thay thế nhật ký trong database. Quan hệ không cascade delete để bảo toàn thông tin xử lý.

| Cột | Java | SQL / ràng buộc |
|---|---|---|
| id | Long | bigint IDENTITY PRIMARY KEY |
| booking_id | Booking | bigint NOT NULL REFERENCES bookings(id) |
| actor_id | User | bigint NOT NULL REFERENCES users(id) |
| action | String | nvarchar(100) NOT NULL |
| created_at | LocalDateTime | datetime2 NOT NULL |

## Chỉ mục và tính nhất quán

Unique email, tên loại/sân, code, start_time, payment.booking_id và khóa ghép reservation. Index bookings(booking_date,status), bookings(customer_id,created_at), payments(paid_at) INCLUDE(amount). SQL script tạo schema có XACT_ABORT và transaction. Không thay đổi schema tự động khi khởi động.

## Dữ liệu mẫu

Script `sql/02_sample_data.sql`: 3 sport_types, 20 courts, 17 time_slots, 22 users mẫu (không đăng nhập được), 32 bookings `SAMPLE-*` (20 COMPLETED, 4 PENDING, 3 CONFIRMED, 1 CHECKED_IN, 2 CANCELLED, 2 REJECTED), 47 booking_details, 42 slot_reservations, 20 payments, 91 booking_events; ngày tương đối so với lúc chạy, chạy lại không tạo trùng. DemoData tạo tài khoản demo còn thiếu (23 users) và, chỉ khi bảng đơn rỗng, 20 đơn đã hoàn tất, 20 detail, 20 reservation, 20 payment, 20 event. Dữ liệu mô phỏng có mã DEMO-*. Ngày lùi tương đối để thống kê có dữ liệu thật trong bảng. Các test thêm fixture riêng rồi dọn đúng fixture.
